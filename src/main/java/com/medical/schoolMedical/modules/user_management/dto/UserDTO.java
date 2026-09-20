package com.medical.schoolMedical.modules.user_management.dto;

import com.medical.schoolMedical.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {
    private Long id;
    @NotBlank(message = "số điện thoại không được bỏ trống")
    @Pattern(regexp = "^0\\d{9,10}$", message = "số điện thoại bắt đầu từ 0 và chỉ chứa số và độ dài từ 10-11")
    private String username;
    private String email;
    @NotBlank(message = "password không được bỏ trống")
    @Size(min = 5, max = 30, message = "Mật khẩu có tối thiểu 5 kí tự và tối đa 30")
    @Pattern(regexp = "^\\S+$", message = "Mật khẩu không được chứa khoảng trắng")
    private String password;
    private Role role;
    private boolean isDeleted = false;
}
