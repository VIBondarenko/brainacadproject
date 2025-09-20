package io.github.vibondarenko.clavionx.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.vibondarenko.clavionx.dto.PasswordChangeDto;
import io.github.vibondarenko.clavionx.dto.UserProfileDto;
import io.github.vibondarenko.clavionx.entity.User;
import io.github.vibondarenko.clavionx.repository.UserRepository;

/**
 * Service for user profile management
 * Handles profile updates, password changes, and avatar management
 */
@Service
@Transactional
public class ProfileService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserActivityService activityService;
    private final PasswordHistoryService passwordHistoryService;
    private final PasswordPolicyService passwordPolicyService;

    public ProfileService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        UserActivityService activityService,
        PasswordPolicyService passwordPolicyService,
        PasswordHistoryService passwordHistoryService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.activityService = activityService;
        this.passwordPolicyService = passwordPolicyService;
        this.passwordHistoryService = passwordHistoryService;
    }
    
    /**
     * Get user profile information
     */
    public Optional<UserProfileDto> getUserProfile(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserProfileDto profileDto = new UserProfileDto(
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAge()
            );
            return Optional.of(profileDto);
        }
        
        return Optional.empty();
    }
    
    /**
     * Update user profile information
     */
    public boolean updateUserProfile(String username, UserProfileDto profileDto) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(username);
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                
                // Check if email is already taken by another user
                if (!user.getEmail().equals(profileDto.getEmail()) && userRepository.existsByEmail(profileDto.getEmail())) {
                    return false; // Email already taken
                }
                
                // Update user information
                user.setName(profileDto.getFirstName());
                user.setLastName(profileDto.getLastName());
                user.setEmail(profileDto.getEmail());
                user.setAge(profileDto.getAge());
                user.setPhoneNumber(profileDto.getPhoneNumber());
                
                userRepository.save(user);
                
                // Log activity
                activityService.logActivity(
                    UserActivityService.ActivityType.PROFILE_UPDATE,
                    "Updated profile information",
                    "User",
                    user.getId()
                );
                
                return true;
            }
            
            return false;
        } catch (Exception e) {
            activityService.logFailedActivity(
                "PROFILE_UPDATE",
                "Failed to update profile for user: " + username,
                e.getMessage()
            );
            return false;
        }
    }
    
    /**
     * Change user password
     */
    public boolean changePassword(String username, PasswordChangeDto passwordDto) {
        try {
            // Validate passwords match
            if (!passwordDto.isPasswordsMatching()) {
                return false;
            }
            
            Optional<User> userOptional = userRepository.findByUsername(username);
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                
                // Verify current password
                if (!passwordEncoder.matches(passwordDto.getCurrentPassword(), user.getPassword())) {
                    activityService.logFailedActivity(
                        "PASSWORD_CHANGE",
                        "Invalid current password provided for user: " + username,
                        "Authentication failed"
                    );
                    return false;
                }
                
                // Validate policy and history, then encode and set new password
                passwordPolicyService.validateOrThrow(passwordDto.getNewPassword());
                passwordHistoryService.validateNotSameAsCurrent(user, passwordDto.getNewPassword());
                passwordHistoryService.validateNotInHistory(user, passwordDto.getNewPassword());
                String encodedNewPassword = passwordEncoder.encode(passwordDto.getNewPassword());
                user.setPassword(encodedNewPassword);
                passwordHistoryService.storePasswordHash(user, encodedNewPassword);
                
                userRepository.save(user);
                
                // Log successful password change
                activityService.logActivity(
                    UserActivityService.ActivityType.PASSWORD_CHANGE,
                    "Password changed successfully",
                    "User",
                    user.getId()
                );
                
                return true;
            }
            
            return false;
        } catch (Exception e) {
            activityService.logFailedActivity(
                "PASSWORD_CHANGE",
                "Failed to change password for user: " + username,
                e.getMessage()
            );
            return false;
        }
    }
    
    /**
     * Update user avatar path
     */
    public boolean updateAvatar(String username, String avatarPath) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(username);
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                
                // Update avatar path
                user.setAvatarPath(avatarPath);
                
                // Save user with new avatar path
                userRepository.save(user);
                
                // Log activity
                activityService.logActivity(
                    UserActivityService.ActivityType.PROFILE_UPDATE,
                    "Updated profile avatar: " + avatarPath,
                    "User",
                    user.getId()
                );
                
                return true;
            }
            
            return false;
        } catch (Exception e) {
            activityService.logFailedActivity(
                "AVATAR_UPDATE",
                "Failed to update avatar for user: " + username,
                e.getMessage()
            );
            return false;
        }
    }
    
    /**
     * Get user avatar path
     */
    public String getUserAvatarPath(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.map(User::getAvatarPath).orElse(null);
    }
}



