package com.brainacad.ecs.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brainacad.ecs.entity.TrustedDevice;
import com.brainacad.ecs.entity.User;
import com.brainacad.ecs.repository.TrustedDeviceRepository;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Service for managing trusted devices for 2FA bypass
 */
@Service
@Transactional
public class TrustedDeviceService {

    private static final Logger logger = LoggerFactory.getLogger(TrustedDeviceService.class);
    private static final String USER_AGENT_HEADER = "User-Agent";
    private static final String CHROME_BROWSER = "Chrome";
    private static final String FIREFOX_BROWSER = "Firefox";
    private static final String EDGE_BROWSER = "Edge";
    private static final String SAFARI_BROWSER = "Safari";
    private static final String UNKNOWN_DEVICE = "Unknown Device";
    private static final String WINDOWS_PREFIX = "Windows - ";
    private static final String MAC_PREFIX = "Mac - ";
    private static final String LINUX_PREFIX = "Linux - ";
    
    private final TrustedDeviceRepository trustedDeviceRepository;

    public TrustedDeviceService(TrustedDeviceRepository trustedDeviceRepository) {
        this.trustedDeviceRepository = trustedDeviceRepository;
    }

    /**
     * Check if current device is trusted for the user
     */
    public boolean isDeviceTrusted(User user, HttpServletRequest request) {
        String deviceIdentifier = generateDeviceIdentifier(request);
        
        Optional<TrustedDevice> trustedDevice = trustedDeviceRepository
            .findValidTrustedDevice(user, deviceIdentifier, LocalDateTime.now());
            
        if (trustedDevice.isPresent()) {
            // Update last used timestamp
            TrustedDevice device = trustedDevice.get();
            device.updateLastUsed();
            trustedDeviceRepository.save(device);
            
            logger.info("Device {} is trusted for user {}", deviceIdentifier, user.getUsername());
            return true;
        }
        
        logger.debug("Device {} is not trusted for user {}", deviceIdentifier, user.getUsername());
        return false;
    }

    /**
     * Mark current device as trusted for the user
     */
    public void trustDevice(User user, HttpServletRequest request) {
        String deviceIdentifier = generateDeviceIdentifier(request);
        String deviceName = generateDeviceName(request);
        String userAgent = request.getHeader(USER_AGENT_HEADER);
        String ipAddress = getClientIpAddress(request);
        
        // Check if device already exists
        Optional<TrustedDevice> existingDevice = trustedDeviceRepository
            .findByUserAndDeviceIdentifier(user, deviceIdentifier);
            
        if (existingDevice.isPresent()) {
            // Reactivate and extend expiration
            TrustedDevice device = existingDevice.get();
            device.setActive(true);
            device.setTrustedAt(LocalDateTime.now());
            device.setExpiresAt(LocalDateTime.now().plusDays(30));
            device.updateLastUsed();
            device.setUserAgent(userAgent);
            device.setIpAddress(ipAddress);
            
            trustedDeviceRepository.save(device);
            logger.info("Reactivated trusted device {} for user {}", deviceIdentifier, user.getUsername());
        } else {
            // Create new trusted device
            TrustedDevice trustedDevice = new TrustedDevice(user, deviceIdentifier, 
                deviceName, userAgent, ipAddress);
            
            trustedDeviceRepository.save(trustedDevice);
            logger.info("Added new trusted device {} for user {}", deviceIdentifier, user.getUsername());
        }
    }

    /**
     * Get all active trusted devices for user
     */
    @Transactional(readOnly = true)
    public List<TrustedDevice> getUserTrustedDevices(User user) {
        return trustedDeviceRepository.findActiveDevicesByUser(user);
    }

    /**
     * Remove trusted device
     */
    public void removeTrustedDevice(User user, Long deviceId) {
        Optional<TrustedDevice> device = trustedDeviceRepository.findById(deviceId);
        
        if (device.isPresent() && device.get().getUser().equals(user)) {
            device.get().setActive(false);
            trustedDeviceRepository.save(device.get());
            logger.info("Removed trusted device {} for user {}", deviceId, user.getUsername());
        }
    }

    /**
     * Remove all trusted devices for user
     */
    public void removeAllTrustedDevices(User user) {
        trustedDeviceRepository.deactivateAllUserDevices(user);
        logger.info("Removed all trusted devices for user {}", user.getUsername());
    }

    /**
     * Generate unique device identifier based on request headers
     */
    private String generateDeviceIdentifier(HttpServletRequest request) {
        String userAgent = request.getHeader(USER_AGENT_HEADER);
        String acceptLanguage = request.getHeader("Accept-Language");
        String acceptEncoding = request.getHeader("Accept-Encoding");
        
        // Combine headers to create device fingerprint
        String fingerprint = userAgent + "|" + acceptLanguage + "|" + acceptEncoding;
        
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(fingerprint.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error generating device identifier", e);
            return fingerprint.hashCode() + "";
        }
    }

    /**
     * Generate human-readable device name
     */
    private String generateDeviceName(HttpServletRequest request) {
        String userAgent = request.getHeader(USER_AGENT_HEADER);
        
        if (userAgent == null) {
            return UNKNOWN_DEVICE;
        }
        
        return detectDeviceFromUserAgent(userAgent);
    }
    
    private String detectDeviceFromUserAgent(String userAgent) {
        // Windows devices
        if (userAgent.contains("Windows")) {
            return detectWindowsBrowser(userAgent);
        }
        
        // Mac devices
        if (userAgent.contains("Mac")) {
            return detectMacBrowser(userAgent);
        }
        
        // Linux devices
        if (userAgent.contains("Linux")) {
            return detectLinuxBrowser(userAgent);
        }
        
        // Mobile devices
        if (userAgent.contains("Android")) {
            return "Android Device";
        }
        
        if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            return "iOS Device";
        }
        
        return UNKNOWN_DEVICE;
    }
    
    private String detectWindowsBrowser(String userAgent) {
        if (userAgent.contains(CHROME_BROWSER)) {
            return WINDOWS_PREFIX + CHROME_BROWSER;
        }
        if (userAgent.contains(FIREFOX_BROWSER)) {
            return WINDOWS_PREFIX + FIREFOX_BROWSER;
        }
        if (userAgent.contains(EDGE_BROWSER)) {
            return WINDOWS_PREFIX + EDGE_BROWSER;
        }
        return "Windows Device";
    }
    
    private String detectMacBrowser(String userAgent) {
        if (userAgent.contains(CHROME_BROWSER)) {
            return MAC_PREFIX + CHROME_BROWSER;
        }
        if (userAgent.contains(SAFARI_BROWSER) && !userAgent.contains(CHROME_BROWSER)) {
            return MAC_PREFIX + SAFARI_BROWSER;
        }
        if (userAgent.contains(FIREFOX_BROWSER)) {
            return MAC_PREFIX + FIREFOX_BROWSER;
        }
        return "Mac Device";
    }
    
    private String detectLinuxBrowser(String userAgent) {
        if (userAgent.contains(CHROME_BROWSER)) {
            return LINUX_PREFIX + CHROME_BROWSER;
        }
        if (userAgent.contains(FIREFOX_BROWSER)) {
            return LINUX_PREFIX + FIREFOX_BROWSER;
        }
        return "Linux Device";
    }

    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0];
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * Scheduled task to clean up expired devices
     * Runs daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredDevices() {
        logger.info("Starting cleanup of expired trusted devices");
        trustedDeviceRepository.deleteExpiredDevices(LocalDateTime.now());
        logger.info("Completed cleanup of expired trusted devices");
    }
}
