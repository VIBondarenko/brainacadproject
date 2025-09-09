package io.github.vibondarenko.clavionx.controller.api;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.vibondarenko.clavionx.entity.UserActivity;
import io.github.vibondarenko.clavionx.service.UserActivityService;

/**
 * Integration tests for Activity API Controller
 */
@WebMvcTest(ActivityApiController.class)
@AutoConfigureMockMvc(addFilters = true)
class ActivityApiControllerTest {    
@Autowired
private MockMvc mockMvc;

	@org.mockito.Mock
	private UserActivityService activityService;



	private UserActivity sampleActivity;
	private List<UserActivity> sampleActivities;
	private Page<UserActivity> samplePage;

	@BeforeEach
	@SuppressWarnings("unused")
	void setUp() {
		sampleActivity = UserActivity.builder()
				.userId(100L)
				.username("testuser")
				.actionType("VIEW_COURSE")
				.actionDescription("Viewed course details")
				.resourceType("Course")
				.resourceId(1L)
				.ipAddress("192.168.1.100")
				.userAgent("Mozilla/5.0")
				.status("SUCCESS")
				.build();

		sampleActivities = Arrays.asList(sampleActivity);
		samplePage = new PageImpl<>(sampleActivities);
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void getUserActivities_WithAdminRole_ShouldReturnActivities() throws Exception {
		// Given
		when(activityService.getUserActivities(anyLong(), any(Pageable.class)))
				.thenReturn(samplePage);

		// When & Then
		mockMvc.perform(get("/api/v1/activities/user/100")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content[0].username").value("testuser"))
				.andExpect(jsonPath("$.content[0].actionType").value("VIEW_COURSE"));
	}


	@Test
	@WithMockUser(roles = "ANALYST")
	void getRecentActivities_WithAnalystRole_ShouldReturnActivities() throws Exception {
		// Given
		when(activityService.getRecentActivities(anyInt()))
				.thenReturn(sampleActivities);

		// When & Then
		mockMvc.perform(get("/api/v1/activities/recent")
						.param("hours", "24")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].username").value("testuser"));
	}

	@Test
	@WithMockUser(roles = "MANAGER")
	void getActivityStatistics_WithManagerRole_ShouldReturnStatistics() throws Exception {
		// Given
		List<Object[]> stats = Arrays.asList(
				new Object[]{"VIEW_COURSE", 10L},
				new Object[]{"CREATE_COURSE", 5L}
		);
		when(activityService.getActivityStatistics()).thenReturn(stats);

		// When & Then
		mockMvc.perform(get("/api/v1/activities/statistics")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0][0]").value("VIEW_COURSE"))
				.andExpect(jsonPath("$[0][1]").value(10));
	}

	@Test
	@WithMockUser(roles = "ANALYST")
	void getTopActiveUsers_WithAnalystRole_ShouldReturnTopUsers() throws Exception {
		// Given
		List<Object[]> topUsers = Arrays.asList(
				new Object[]{"admin", 50L},
				new Object[]{"user1", 30L}
		);
		when(activityService.getTopActiveUsers(anyInt())).thenReturn(topUsers);

		// When & Then
		mockMvc.perform(get("/api/v1/activities/top-users")
						.param("days", "7")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0][0]").value("admin"))
				.andExpect(jsonPath("$[0][1]").value(50));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void getSuspiciousActivities_WithAdminRole_ShouldReturnSuspiciousActivities() throws Exception {
		// Given
		UserActivity suspiciousActivity = UserActivity.builder()
				.username("suspicious")
				.actionType("LOGIN")
				.actionDescription("Failed login attempt")
				.ipAddress("192.168.1.200")
				.status("FAILURE")
				.build();

		when(activityService.getSuspiciousActivities(anyString(), anyInt()))
				.thenReturn(Arrays.asList(suspiciousActivity));

		// When & Then
		mockMvc.perform(get("/api/v1/activities/suspicious")
						.param("ipAddress", "192.168.1.200")
						.param("hours", "24")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].username").value("suspicious"))
				.andExpect(jsonPath("$[0].status").value("FAILURE"));
	}


	@Test
	@WithMockUser(roles = "SUPER_ADMIN")
	void cleanupOldActivities_WithSuperAdminRole_ShouldReturnSuccess() throws Exception {
		// When & Then
		mockMvc.perform(post("/api/v1/activities/cleanup")
						.param("daysToKeep", "90")
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("success"))
				.andExpect(jsonPath("$.message").exists());
	}


	@Test
	void getActivitiesWithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
		// When & Then
		mockMvc.perform(get("/api/v1/activities/user/100")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(roles = "ANALYST")
	void getRecentActivities_WithDefaultHours_ShouldUseDefault24Hours() throws Exception {
		// Given
		when(activityService.getRecentActivities(24))
				.thenReturn(sampleActivities);

		// When & Then
		mockMvc.perform(get("/api/v1/activities/recent")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	@WithMockUser(roles = "SUPER_ADMIN")
	void cleanupOldActivities_WithDefaultDays_ShouldUseDefault90Days() throws Exception {
		// When & Then
		mockMvc.perform(post("/api/v1/activities/cleanup")
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Cleanup completed for activities older than 90 days"));
	}
}
