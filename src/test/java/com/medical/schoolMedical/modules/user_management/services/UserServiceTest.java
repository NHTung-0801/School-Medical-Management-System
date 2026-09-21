package com.medical.schoolMedical.modules.user_management.services;

import com.medical.schoolMedical.enums.Role;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.exceptions.ErrorCode;
import com.medical.schoolMedical.modules.user_management.dto.UserDTO;
import com.medical.schoolMedical.modules.user_management.entities.User;
import com.medical.schoolMedical.modules.user_management.mappers.UserMapper;
import com.medical.schoolMedical.modules.user_management.repositories.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ParentRepository parentRepository;
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private SchoolNurseRepository schoolNurseRepository;
    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should sign up user successfully when username is not taken")
    void testSignUpSuccess() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("new_nurse");
        userDTO.setPassword("P@ssword123");
        userDTO.setRole(Role.NURSE);

        User user = new User();
        user.setUsername("new_nurse");
        user.setPassword("P@ssword123");
        user.setRole(Role.NURSE);

        when(userRepository.existsByUsername("new_nurse")).thenReturn(false);
        when(userMapper.toUser(userDTO)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserDTO(any(User.class))).thenReturn(userDTO);

        UserDTO result = userService.signUp(userDTO);

        assertNotNull(result);
        assertEquals("new_nurse", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when signing up with existing username")
    void testSignUpUsernameExists() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("existing_user");
        userDTO.setPassword("P@ssword123");

        when(userRepository.existsByUsername("existing_user")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.signUp(userDTO);
        });

        assertEquals(ErrorCode.USERNAME_EXISTS, exception.getErrorCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should reset password successfully when user exists")
    void testResetPasswordSuccess() {
        User user = new User();
        user.setId(10L);
        user.setUsername("student_parent");
        user.setPassword("oldEncodedPass");

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("NewSecret123")).thenReturn("newEncodedPass");

        userService.resetPassword(10L, "NewSecret123");

        assertEquals("newEncodedPass", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should throw BusinessException when resetting password for non-existent user")
    void testResetPasswordUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.resetPassword(999L, "NewSecret123");
        });

        assertEquals(ErrorCode.USER_NOT_EXISTS, exception.getErrorCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should pass validateUserInput when parent has valid Vietnamese phone number")
    void testValidateUserInputValidParentPhone() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("0912345678");
        userDTO.setPassword("Pass1234");
        userDTO.setRole(Role.PARENT);

        assertDoesNotThrow(() -> userService.validateUserInput(userDTO));
    }

    @Test
    @DisplayName("Should throw INVALID_PHONE_NUMBER when parent has invalid phone number")
    void testValidateUserInputInvalidParentPhone() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("1234567890"); // Invalid prefix
        userDTO.setPassword("Pass1234");
        userDTO.setRole(Role.PARENT);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.validateUserInput(userDTO);
        });

        assertEquals(ErrorCode.INVALID_PHONE_NUMBER, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should pass validateUserInput for root admin with username 'admin'")
    void testValidateUserInputAdminAllowed() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("admin");
        userDTO.setPassword("AdminPass123");
        userDTO.setRole(Role.ADMIN);

        assertDoesNotThrow(() -> userService.validateUserInput(userDTO));
    }

    @Test
    @DisplayName("Should create PARENT entity and sync phone number to Parent entity")
    void testCreateUserParentSyncsPhoneNumber() {
        User user = new User();
        user.setId(5L);
        user.setUsername("0987654321");
        user.setRole(Role.PARENT);

        when(userRepository.save(user)).thenReturn(user);

        userService.createUser(user);

        verify(userRepository, times(1)).save(user);
        org.mockito.ArgumentCaptor<com.medical.schoolMedical.modules.user_management.entities.Parent> parentCaptor =
                org.mockito.ArgumentCaptor.forClass(com.medical.schoolMedical.modules.user_management.entities.Parent.class);
        verify(parentRepository, times(1)).save(parentCaptor.capture());
        assertEquals("0987654321", parentCaptor.getValue().getPhoneNumber());
        assertEquals(user, parentCaptor.getValue().getUser());
    }
}
