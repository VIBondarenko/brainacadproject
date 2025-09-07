package io.github.vibondarenko.clavionx.controller.api;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.vibondarenko.clavionx.entity.UserActivity;
import io.github.vibondarenko.clavionx.service.UserActivityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST API Controller for User Activity Management
 * Provides endpoints for activity tracking and analytics
 */
@RestController
@RequestMapping("/api/v1/activities")
@Tag(name = "Activity Management", description = "Operations for user activity tracking and analytics")
@SecurityRequirement(name = "session-auth")
public class ActivityApiController {

	private final UserActivityService activityService;

	public ActivityApiController(UserActivityService activityService) {
		this.activityService = activityService;
	}

	@Operation(
		summary = "Get user activities",
		description = "Retrieve paginated list of activities for a specific user. Requires ADMIN role or higher."
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved activities",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = UserActivity.class))),
			@ApiResponse(responseCode = "403", description = "Access denied",
					content = @Content)
	})
	@GetMapping("/user/{userId}")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
	public ResponseEntity<Page<UserActivity>> getUserActivities(
			@Parameter(description = "User ID", required = true, example = "1")
			@PathVariable Long userId,
			@Parameter(description = "Pagination parameters") Pageable pageable) {
		
		Page<UserActivity> activities = activityService.getUserActivities(userId, pageable);
		return ResponseEntity.ok(activities);
	}

	@Operation(
			summary = "Get recent activities",
			description = "Retrieve recent activities across all users. Requires ADMIN role or higher."
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved recent activities",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = UserActivity.class)))
	})
	@GetMapping("/recent")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'ANALYST')")
	public ResponseEntity<List<UserActivity>> getRecentActivities(
			@Parameter(description = "Number of hours to look back", example = "24")
			@RequestParam(defaultValue = "24") int hours) {
		
		List<UserActivity> activities = activityService.getRecentActivities(hours);
		return ResponseEntity.ok(activities);
	}

	@Operation(
			summary = "Get activity statistics",
			description = "Retrieve activity statistics grouped by action type. Requires ANALYST role or higher."
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved statistics")
	})
	@GetMapping("/statistics")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'ANALYST')")
	public ResponseEntity<List<Object[]>> getActivityStatistics() {
		List<Object[]> stats = activityService.getActivityStatistics();
		return ResponseEntity.ok(stats);
	}

	@Operation(
			summary = "Get top active users",
			description = "Retrieve most active users in specified time period. Requires ANALYST role or higher."
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved top active users")
	})
	@GetMapping("/top-users")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'ANALYST')")
	public ResponseEntity<List<Object[]>> getTopActiveUsers(
			@Parameter(description = "Number of days to look back", example = "7")
			@RequestParam(defaultValue = "7") int days) {
		
		List<Object[]> topUsers = activityService.getTopActiveUsers(days);
		return ResponseEntity.ok(topUsers);
	}

	@Operation(
			summary = "Get suspicious activities",
			description = "Retrieve suspicious activities from specific IP address. Requires ADMIN role or higher."
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved suspicious activities")
	})
	@GetMapping("/suspicious")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	public ResponseEntity<List<UserActivity>> getSuspiciousActivities(
			@Parameter(description = "IP address to check", required = true, example = "192.168.1.100")
			@RequestParam String ipAddress,
			@Parameter(description = "Number of hours to look back", example = "24")
			@RequestParam(defaultValue = "24") int hours) {
		
		List<UserActivity> activities = activityService.getSuspiciousActivities(ipAddress, hours);
		return ResponseEntity.ok(activities);
	}

	@Operation(
			summary = "Clean up old activities",
			description = "Delete activities older than specified number of days. Requires SUPER_ADMIN role."
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Cleanup completed successfully")
	})
	@PostMapping("/cleanup")
	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public ResponseEntity<Map<String, String>> cleanupOldActivities(
			@Parameter(description = "Number of days to keep", example = "90")
			@RequestParam(defaultValue = "90") int daysToKeep) {
		
		activityService.cleanupOldActivities(daysToKeep);
		return ResponseEntity.ok(Map.of(
			"status", "success",
			"message", "Cleanup completed for activities older than " + daysToKeep + " days"
		));
	}
}




