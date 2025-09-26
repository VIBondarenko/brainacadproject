package io.github.vibondarenko.clavionx.service;

import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Centralized password policy validation.
 */
@Service
public class PasswordPolicyService {

    @Value("${security.password-policy.min-length:8}")
    private int minLength;

    @Value("${security.password-policy.max-length:100}")
    private int maxLength;

    @Value("${security.password-policy.require-uppercase:true}")
    private boolean requireUppercase;

    @Value("${security.password-policy.require-lowercase:true}")
    private boolean requireLowercase;

    @Value("${security.password-policy.require-digit:true}")
    private boolean requireDigit;

    @Value("${security.password-policy.require-special:true}")
    private boolean requireSpecial;

    @Value("${security.password-policy.forbid-common:true}")
    private boolean forbidCommon;

    private static final Pattern UPPER = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWER = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL = Pattern.compile(".*[^A-Za-z0-9].*");

    private static final Set<String> COMMON_WEAK = Set.of(
        "password","123456","qwerty","111111","12345678","abc123",
        "letmein","123456789","12345","password1","admin","welcome"
    );

    /**
     * Validate password and throw IllegalArgumentException if invalid.
     * @param password Password to validate
     * @throws IllegalArgumentException if password is invalid
     */
    public void validateOrThrow(String password) {
        String msg = validate(password);
        if (msg != null) {
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Returns null if valid, otherwise error message.
     * @param password Password to validate
     * @return null if valid, otherwise error message
     */
    public String validate(String password) {
        if (password == null || password.isBlank()) {
            return "Password is required";
        }
        int len = password.length();
        if (len < minLength || len > maxLength) {
            return String.format("Password must be between %d and %d characters", minLength, maxLength);
        }
        if (requireUppercase && !UPPER.matcher(password).matches()) {
            return "Password must contain at least one uppercase letter";
        }
        if (requireLowercase && !LOWER.matcher(password).matches()) {
            return "Password must contain at least one lowercase letter";
        }
        if (requireDigit && !DIGIT.matcher(password).matches()) {
            return "Password must contain at least one digit";
        }
        if (requireSpecial && !SPECIAL.matcher(password).matches()) {
            return "Password must contain at least one special character";
        }
        if (forbidCommon && COMMON_WEAK.contains(password.toLowerCase())) {
            return "Password is too common; choose a stronger one";
        }
        return null;
    }
}