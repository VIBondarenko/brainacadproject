package com.brainacad.ecs.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brainacad.ecs.entity.TwoFactorToken;
import com.brainacad.ecs.entity.User;
import com.brainacad.ecs.repository.TwoFactorTokenRepository;
import com.brainacad.ecs.repository.UserRepository;
import com.brainacad.ecs.security.TwoFactorMethod;

/**
 * Service for managing two-factor authentication
 */
@Service
@Transactional
public class TwoFactorService {

    private final TwoFactorTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final UserActivityService activityService;
    
    // Configuration constants
    private static final int TOKEN_VALIDITY_MINUTES = 10;
    private static final int MAX_ATTEMPTS = 3;
    private static final int TOKEN_LENGTH = 6;
    
    private final SecureRandom secureRandom = new SecureRandom();

    public TwoFactorService(
        TwoFactorTokenRepository tokenRepository,
        UserRepository userRepository,
        EmailService emailService,
        UserActivityService activityService
    ) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.activityService = activityService;
    }

    /**
     * Generate and send 2FA verification code
     * 
     * @param user User to send code to
     * @param method Method to use (EMAIL, PHONE)
     * @return true if code was sent successfully
     */
    public boolean generateAndSendCode(User user, TwoFactorMethod method) {
        try {
            // Invalidate any existing active tokens for the user
            tokenRepository.invalidateActiveTokensForUser(user, LocalDateTime.now());
            
            // Generate new verification code
            String verificationCode = generateVerificationCode();
            
            // Create token with expiry time
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(TOKEN_VALIDITY_MINUTES);
            TwoFactorToken token = new TwoFactorToken(verificationCode, method, user, expiresAt);
            token.setMaxAttempts(MAX_ATTEMPTS);
            
            // Save token
            tokenRepository.save(token);
            
            // Send code based on method
            boolean sent = false;
            switch (method) {
                case EMAIL:
                    sent = sendCodeViaEmail(user, verificationCode);
                    break;
                case PHONE:
                    sent = sendCodeViaSms(user, verificationCode);
                    break;
                case BOTH:
                    // Send to both email and phone
                    boolean emailSent = sendCodeViaEmail(user, verificationCode);
                    boolean smsSent = sendCodeViaSms(user, verificationCode);
                    sent = emailSent || smsSent; // At least one must succeed
                    break;
            }
            
            if (sent) {
                // Log successful code generation
                activityService.logActivity(
                    UserActivityService.ActivityType.TWO_FACTOR_CODE_SENT,
                    "Two-factor authentication code sent via " + method.getDisplayName(),
                    "User",
                    user.getId()
                );
            } else {
                // Mark token as used if sending failed
                token.setUsed(true);
                tokenRepository.save(token);
                
                activityService.logFailedActivity(
                    "TWO_FACTOR_CODE_FAILED",
                    "Failed to send two-factor code via " + method.getDisplayName(),
                    "Code sending failed"
                );
            }
            
            return sent;
            
        } catch (Exception e) {
            activityService.logFailedActivity(
                "TWO_FACTOR_CODE_ERROR",
                "Error generating two-factor code for user: " + user.getUsername(),
                e.getMessage()
            );
            return false;
        }
    }

    /**
     * Verify 2FA code entered by user
     * 
     * @param user User attempting verification
     * @param code Code entered by user
     * @return true if code is valid and verified
     */
    public boolean verifyCode(User user, String code) {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // Find active token for user and code
            Optional<TwoFactorToken> tokenOpt = tokenRepository.findActiveTokenByUserAndCode(user, code, now);
            
            if (tokenOpt.isEmpty()) {
                // Log failed verification attempt
                activityService.logFailedActivity(
                    "TWO_FACTOR_VERIFICATION_FAILED",
                    "Invalid two-factor code entered for user: " + user.getUsername(),
                    "Code not found or expired"
                );
                return false;
            }
            
            TwoFactorToken token = tokenOpt.get();
            
            // Check if token is still valid
            if (!token.isValid()) {
                activityService.logFailedActivity(
                    "TWO_FACTOR_VERIFICATION_FAILED",
                    "Invalid two-factor token for user: " + user.getUsername(),
                    "Token expired or exceeded max attempts"
                );
                return false;
            }
            
            // Mark token as used
            token.setUsed(true);
            tokenRepository.save(token);
            
            // Log successful verification
            activityService.logActivity(
                UserActivityService.ActivityType.TWO_FACTOR_VERIFIED,
                "Two-factor authentication successfully verified",
                "User",
                user.getId()
            );
            
            return true;
            
        } catch (Exception e) {
            activityService.logFailedActivity(
                "TWO_FACTOR_VERIFICATION_ERROR",
                "Error verifying two-factor code for user: " + user.getUsername(),
                e.getMessage()
            );
            return false;
        }
    }

    /**
     * Record failed verification attempt
     * 
     * @param user User who attempted verification
     * @param code Code that was attempted
     */
    public void recordFailedAttempt(User user, String code) {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // Try to find the token and increment attempts
            Optional<TwoFactorToken> tokenOpt = tokenRepository.findActiveTokenByUserAndCode(user, code, now);
            
            if (tokenOpt.isPresent()) {
                TwoFactorToken token = tokenOpt.get();
                token.incrementAttempts();
                tokenRepository.save(token);
                
                if (token.hasExceededMaxAttempts()) {
                    activityService.logFailedActivity(
                        "TWO_FACTOR_MAX_ATTEMPTS",
                        "Two-factor authentication max attempts exceeded for user: " + user.getUsername(),
                        "Token blocked after " + token.getMaxAttempts() + " attempts"
                    );
                }
            }
            
            activityService.logFailedActivity(
                "TWO_FACTOR_FAILED_ATTEMPT",
                "Failed two-factor authentication attempt for user: " + user.getUsername(),
                "Invalid code entered"
            );
            
        } catch (Exception e) {
            activityService.logFailedActivity(
                "TWO_FACTOR_ATTEMPT_ERROR",
                "Error recording failed attempt for user: " + user.getUsername(),
                e.getMessage()
            );
        }
    }

    /**
     * Enable 2FA for user
     */
    public boolean enableTwoFactor(User user, TwoFactorMethod method) {
        try {
            user.setTwoFactorEnabled(true);
            user.setPreferredTwoFactorMethod(method);
            userRepository.save(user);
            
            activityService.logActivity(
                UserActivityService.ActivityType.TWO_FACTOR_ENABLED,
                "Two-factor authentication enabled with method: " + method.getDisplayName(),
                "User",
                user.getId()
            );
            
            return true;
        } catch (Exception e) {
            activityService.logFailedActivity(
                "TWO_FACTOR_ENABLE_ERROR",
                "Error enabling two-factor authentication for user: " + user.getUsername(),
                e.getMessage()
            );
            return false;
        }
    }

    /**
     * Disable 2FA for user
     */
    public boolean disableTwoFactor(User user) {
        try {
            user.setTwoFactorEnabled(false);
            user.setPreferredTwoFactorMethod(null);
            userRepository.save(user);
            
            // Invalidate any active tokens
            tokenRepository.invalidateActiveTokensForUser(user, LocalDateTime.now());
            
            activityService.logActivity(
                UserActivityService.ActivityType.TWO_FACTOR_DISABLED,
                "Two-factor authentication disabled",
                "User",
                user.getId()
            );
            
            return true;
        } catch (Exception e) {
            activityService.logFailedActivity(
                "TWO_FACTOR_DISABLE_ERROR",
                "Error disabling two-factor authentication for user: " + user.getUsername(),
                e.getMessage()
            );
            return false;
        }
    }

    /**
     * Check if user has active 2FA tokens
     */
    public boolean hasActiveTokens(User user) {
        return tokenRepository.hasActiveTokens(user, LocalDateTime.now());
    }

    /**
     * Get recent tokens for user (for admin/debugging purposes)
     */
    public List<TwoFactorToken> getRecentTokensForUser(User user) {
        return tokenRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Cleanup expired and old used tokens (scheduled task)
     */
    @Transactional
    public void cleanupTokens() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime cleanupBefore = now.minusDays(7); // Keep used tokens for 7 days
            
            // Delete expired tokens
            tokenRepository.deleteExpiredTokens(now);
            
            // Delete old used tokens
            tokenRepository.deleteUsedTokensOlderThan(cleanupBefore);
            
        } catch (Exception e) {
            activityService.logFailedActivity(
                "TWO_FACTOR_CLEANUP_ERROR",
                "Error during two-factor token cleanup",
                e.getMessage()
            );
        }
    }

    /**
     * Generate secure 6-digit verification code
     */
    private String generateVerificationCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            code.append(secureRandom.nextInt(10));
        }
        return code.toString();
    }

    /**
     * Send verification code via email
     */
    private boolean sendCodeViaEmail(User user, String code) {
        try {
            String subject = "Your Two-Factor Authentication Code";
            String text = String.format(
                "Hi %s,\n\n" +
                "Your verification code is: %s\n\n" +
                "This code will expire in %d minutes.\n" +
                "If you didn't request this code, please contact support.\n\n" +
                "Best regards,\n" +
                "Education System Team",
                user.getName(), code, TOKEN_VALIDITY_MINUTES
            );
            
            emailService.sendSimpleMessage(user.getEmail(), subject, text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Send verification code via SMS
     * TODO: Implement SMS service integration (Twilio, AWS SNS, etc.)
     */
    private boolean sendCodeViaSms(User user, String code) {
        // For now, return false as SMS service is not implemented
        // In a real implementation, this would integrate with SMS provider
        
        String phoneNumber = user.getPhoneNumber();
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        
        // TODO: Integrate with SMS service
        // Example implementation would be:
        // try {
        //     smsService.sendMessage(phoneNumber, "Your verification code: " + code);
        //     return true;
        // } catch (Exception e) {
        //     return false;
        // }
        
        // For development/testing, just log that we would send SMS
        System.out.println("SMS would be sent to " + phoneNumber + " with code: " + code);
        return true; // Return true for testing purposes
    }
}