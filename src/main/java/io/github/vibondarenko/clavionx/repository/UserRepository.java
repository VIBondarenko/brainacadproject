package io.github.vibondarenko.clavionx.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.vibondarenko.clavionx.entity.User;
import io.github.vibondarenko.clavionx.security.Role;

/**
 * Repository interface for User entities
 * Provides database operations for user authentication and management
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username (case-insensitive)
     * @param username the username to search for
     * @return an Optional containing the found User or empty if not found
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.username) = LOWER(:username)")
    Optional<User> findByUsername(@Param("username") String username);
    
    /**
     * Find user by username (exact match)
     * @param username the username to search for
     * @return an Optional containing the found User or empty if not found
     */
    Optional<User> findByUsernameIgnoreCase(String username);
    
    /**
     * Find user by email (case-insensitive) - email is now in Person class
     * @param email the email to search for
     * @return an Optional containing the found User or empty if not found
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * Find user by username or email (for flexible login)
     * @param login the username or email to search for
     * @return an Optional containing the found User or empty if not found
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.username) = LOWER(:login) OR LOWER(u.email) = LOWER(:login)")
    Optional<User> findByUsernameOrEmail(@Param("login") String login);
    
    /**
     * Find all users by role
     * @param role the role to filter users by
     * @return a list of users with the specified role
     */
    List<User> findByRole(Role role);
    
    /**
     * Find all enabled users
     * @return a list of enabled users
     */
    List<User> findByEnabledTrue();
    
    /**
     * Find all disabled users
     * @return a list of disabled users
     */
    List<User> findByEnabledFalse();
    
    /**
     * Find users by name containing (case-insensitive)
     * @param name the name substring to search for
     * @return a list of users whose name or last name contains the specified substring
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Check if username exists
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if email exists
     * @param email the email to check
     * @return true if the email exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Count users by role
     * @param role the role to count users for
     * @return the number of users with the specified role
     */
    long countByRole(Role role);
    
    /**
     * Find users by role and enabled status
     * @param role the role to filter users by
     * @param enabled the enabled status to filter users by
     * @return a list of users matching the specified role and enabled status
     */
    List<User> findByRoleAndEnabled(Role role, boolean enabled);
    
    /**
     * Find users with specific role hierarchy
     * @param roles the list of roles to filter users by
     * @return a list of users whose roles are in the specified list
     */
    @Query("SELECT u FROM User u WHERE u.role IN :roles")
    List<User> findByRoles(@Param("roles") List<Role> roles);
}