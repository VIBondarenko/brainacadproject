package io.github.vibondarenko.clavionx.security;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import io.github.vibondarenko.clavionx.entity.User;
import io.github.vibondarenko.clavionx.repository.UserRepository;
import io.github.vibondarenko.clavionx.service.TwoFactorService;

/**
 * Authentication provider for handling two-factor authentication
 */
@Component
public class TwoFactorAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final TwoFactorService twoFactorService;

    public TwoFactorAuthenticationProvider(UserRepository userRepository, 
                                         TwoFactorService twoFactorService) {
        this.userRepository = userRepository;
        this.twoFactorService = twoFactorService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        
        if (!(authentication instanceof TwoFactorAuthenticationToken)) {
            return null;
        }

        TwoFactorAuthenticationToken token = (TwoFactorAuthenticationToken) authentication;
        String username = token.getUsername();

        // Find user
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new BadCredentialsException("User not found: " + username);
        }

        User user = userOptional.get();

        // If token is already authenticated (after 2FA verification), return it
        if (token.isAuthenticated()) {
            return token;
        }

        // For unauthenticated token, check if user requires 2FA
        if (user.isTwoFactorEnabled()) {
            // Generate and send 2FA code
            TwoFactorMethod method = user.getPreferredTwoFactorMethod();
            if (method == null) {
                method = TwoFactorMethod.EMAIL; // Default fallback
            }

            boolean codeSent = twoFactorService.generateAndSendCode(user, method);
            if (!codeSent) {
                throw new BadCredentialsException("Failed to send two-factor authentication code");
            }

            // Return unauthenticated token to indicate 2FA is required
            return new TwoFactorAuthenticationToken(username);
        }

        // If 2FA is not enabled, create authenticated token
        return new TwoFactorAuthenticationToken(user, username, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return TwoFactorAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * Verify 2FA code and complete authentication
     * 
     * @param username Username of the user
     * @param code Verification code entered by user
     * @return Authenticated token if code is valid, null otherwise
     */
    public Authentication verifyTwoFactorCode(String username, String code) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isEmpty()) {
                return null;
            }

            User user = userOptional.get();
            
            // Verify the code
            boolean verified = twoFactorService.verifyCode(user, code);
            if (!verified) {
                // Record failed attempt
                twoFactorService.recordFailedAttempt(user, code);
                return null;
            }

            // Create authenticated token
            return new TwoFactorAuthenticationToken(user, username, user.getAuthorities());
            
        } catch (Exception e) {
            return null;
        }
    }
}



