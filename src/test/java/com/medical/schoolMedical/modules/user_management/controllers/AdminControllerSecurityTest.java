package com.medical.schoolMedical.modules.user_management.controllers;

import com.medical.schoolMedical.modules.user_management.services.StatisticsService;
import com.medical.schoolMedical.modules.user_management.services.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @MockBean
    private StudentService studentService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("User with ROLE_ADMIN should access admin dashboard successfully")
    void testAdminAccessWithAdminRole() throws Exception {
        when(statisticsService.getMonthlyVaccinationCounts(anyInt())).thenReturn(List.of(0, 0, 0));
        when(statisticsService.getMonthlyHealthCheckCounts(anyInt())).thenReturn(List.of(0, 0, 0));
        when(statisticsService.getMonthlyMedicalEventCounts(anyInt())).thenReturn(List.of(0, 0, 0));
        when(statisticsService.getUserCountsByRole()).thenReturn(java.util.Map.of(
                "parents", 10L,
                "students", 20L,
                "nurses", 5L,
                "medicines", 30L
        ));
        when(studentService.getStudentsCreatedThisMonth()).thenReturn(Collections.emptyList());
        when(studentService.getStudentsCreatedLastMonth()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"));
    }

    @Test
    @WithMockUser(username = "parentUser", roles = {"PARENT"})
    @DisplayName("User with ROLE_PARENT should be denied access to admin dashboard")
    void testAdminAccessWithParentRoleDenied() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access-denied"));
    }

    @Test
    @WithMockUser(username = "nurseUser", roles = {"NURSE"})
    @DisplayName("User with ROLE_NURSE should be denied access to admin dashboard")
    void testAdminAccessWithNurseRoleDenied() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access-denied"));
    }
}
