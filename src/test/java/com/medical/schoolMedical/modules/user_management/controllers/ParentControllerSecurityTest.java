package com.medical.schoolMedical.modules.user_management.controllers;

import com.medical.schoolMedical.enums.Role;
import com.medical.schoolMedical.modules.user_management.entities.User;
import com.medical.schoolMedical.security.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ParentControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Authenticated parent with CustomUserDetails should access parent home successfully")
    void testParentAccessWithParentRole() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("parentUser");
        user.setRole(Role.PARENT);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        mockMvc.perform(get("/parent/parent-home").with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("parent/parent-home"));
    }

    @Test
    @WithMockUser(username = "nurseUser", roles = {"NURSE"})
    @DisplayName("User with ROLE_NURSE should be denied access to /parent/**")
    void testParentAccessWithNurseRoleDenied() throws Exception {
        mockMvc.perform(get("/parent/parent-home"))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access-denied"));
    }

    @Test
    @DisplayName("Unauthenticated request to /parent/parent-home should redirect to /login")
    void testParentAccessUnauthenticatedRedirect() throws Exception {
        mockMvc.perform(get("/parent/parent-home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
}
