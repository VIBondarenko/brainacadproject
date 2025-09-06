package com.brainacad.ecs.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brainacad.ecs.dto.PasswordChangeDto;
import com.brainacad.ecs.dto.UserProfileDto;
import com.brainacad.ecs.entity.User;
import com.brainacad.ecs.repository.UserRepository;

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

    public ProfileService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        UserActivityService activityService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.activityService = activityService;
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
                
                // Encode and set new password
                String encodedNewPassword = passwordEncoder.encode(passwordDto.getNewPassword());
                user.setPassword(encodedNewPassword);
                
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
