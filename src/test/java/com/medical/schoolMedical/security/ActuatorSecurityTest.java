package com.medical.schoolMedical.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ActuatorSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Anonymous user should be able to access /actuator/health")
    void testActuatorHealth_Public() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("Unauthenticated access to /actuator/metrics should be redirected to login")
    void testActuatorMetrics_UnauthenticatedRedirect() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "parentUser", roles = {"PARENT"})
    @DisplayName("Parent user should be denied access (403) to /actuator/metrics")
    void testActuatorMetrics_ParentForbidden() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "adminUser", roles = {"ADMIN"})
    @DisplayName("Admin user should be authorized to access /actuator/metrics")
    void testActuatorMetrics_AdminAuthorized() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.names").isArray());
    }
}
