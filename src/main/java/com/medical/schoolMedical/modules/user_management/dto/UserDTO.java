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
    @NotBlank(message = "Số điện thoại đăng nhập không được bỏ trống")
    @Pattern(regexp = "^(0[35789]\\d{8}|admin)$", message = "Số điện thoại đăng nhập phải là số điện thoại hợp lệ gồm 10 chữ số (bắt đầu bằng 03, 05, 07, 08, 09)")
    private String username;

    @NotBlank(message = "Email không được bỏ trống")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,6}$", message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Mật khẩu không được bỏ trống")
    @Size(min = 5, max = 30, message = "Mật khẩu có tối thiểu 5 kí tự và tối đa 30")
    @Pattern(regexp = "^\\S+$", message = "Mật khẩu không được chứa khoảng trắng")
    private String password;
    private Role role;
    private boolean isDeleted = false;
}
