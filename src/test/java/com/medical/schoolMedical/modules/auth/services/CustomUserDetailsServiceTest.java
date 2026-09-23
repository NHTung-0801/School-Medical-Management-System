package com.medical.schoolMedical.modules.auth.services;

import com.medical.schoolMedical.enums.Role;
import com.medical.schoolMedical.modules.user_management.entities.User;
import com.medical.schoolMedical.modules.user_management.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Should load user by username when user exists")
    void testLoadUserByUsernameSuccess() {
        User user = new User();
        user.setId(1L);
        user.setUsername("nurse_lan");
        user.setPassword("encodedPassword123");
        user.setRole(Role.NURSE);

        when(userRepository.findByUsername("nurse_lan")).thenReturn(user);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("nurse_lan");

        assertNotNull(userDetails);
        assertEquals("nurse_lan", userDetails.getUsername());
        assertEquals("encodedPassword123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_NURSE")));
        verify(userRepository, times(1)).findByUsername("nurse_lan");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByUsername("unknown_user")).thenReturn(null);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("unknown_user");
        });

        assertTrue(exception.getMessage().contains("unknown_user"));
        verify(userRepository, times(1)).findByUsername("unknown_user");
    }
}
