package com.medical.schoolMedical.service;

import com.medical.schoolMedical.dto.UserDTO;
import com.medical.schoolMedical.entities.*;
import com.medical.schoolMedical.enums.Role;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.exceptions.ErrorCode;
import com.medical.schoolMedical.mapper.UserMapper;
import com.medical.schoolMedical.repositories.*;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
public class UserService {
    UserRepository userRepository;
    ParentRepository parentRepository;
    AdminRepository adminRepository;
    SchoolNurseRepository schoolNurseRepository;
    ManagerRepository managerRepository;
    UserMapper userMapper;
    StudentRepository studentRepository;
    MedicalEventRepository medicalEventRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    public UserDTO signUp(UserDTO userDTO) {
        if(userRepository.existsByUsername(userDTO.getUsername())){
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }

        User user = userMapper.toUser(userDTO);

        //        Xử dụng thuật toán hash BCrypt để hash password
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try{
            return userMapper.toUserDTO(createUser(user));
        }catch (Exception ex){
            ex.printStackTrace(); //kiểm tra ếu lôỗi là user_id null
            //            Bắt các lỗi ngoài ý muốn
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }
    }

    public void validateUserInput(UserDTO userDTO) {
        String password = userDTO.getPassword().trim();
        if (password.contains("<script>") || password.matches(".*[<>\"'].+")) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        // gán lại username đã clean
        userDTO.setPassword(password);
    }

    @Transactional
    public User createUser(User user){
            userRepository.save(user);
            switch (user.getRole()){
                case MANAGER:
                    Manager manager = new Manager();
                    manager.setUser(user);
                    managerRepository.save(manager);
                    break;
                case NURSE:
                    SchoolNurse schoolNurse = new SchoolNurse();
                    schoolNurse.setUser(user);
                    schoolNurseRepository.save(schoolNurse);
                    break;
                case PARENT:
                    Parent parent = new Parent();
                    parent.setUser(user);
                    parentRepository.save(parent);
                    break;
            }
            return user;
    }


    //Kiểm tra coi đã đăng nhập chưa
    public boolean checkLogin(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return true;
        } else {
            return false;
        }
    }


    @PostConstruct
    public void initAdmin() {
        try {
            if (userRepository.findByUsername("admin") == null) {
//
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("admin")); // Mã hoá mật khẩu
                adminUser.setEmail("admin123@gmail.com");
                adminUser.setRole(Role.ADMIN);
                userRepository.save(adminUser);

                // Tạo và lưu admin vào bảng admins
                Admin admin = new Admin();
                admin.setUser(adminUser);
                admin.setFullName("Admin");
                adminRepository.save(admin);

            }

        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi khởi tạo admin: " + e.getMessage(), e);
        }

    }


    // Lấy tất cả user chưa bị xóa
    public List<User> findAllUsers() {
        return userRepository.findByIsDeletedFalse();
    }

    // Lưu hoặc cập nhật user
    public void saveUser(User user) {
        userRepository.save(user);
    }

    // Tìm theo ID
    public User findById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    // Xóa mềm
    public void softDeleteUser(long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setDeleted(true);
            userRepository.save(user);
        }
    }

    // Tìm theo username
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    //Tìm kiếm admin
    public Admin findAdminByUsername(String username) {
        return adminRepository.findByUser_Username(username)
                .orElse(null);
    }

    public void saveAdmin(Admin admin) {
        adminRepository.save(admin);
    }

    public Parent findParentByUsername(String username) {
        return parentRepository.findByUser_Username(username).orElse(null);
    }

    public void saveParent(Parent parent) {
        parentRepository.save(parent);
    }

    public Manager findManagerByUsername(String username) {
        return managerRepository.findByUser_Username(username).orElse(null);
    }

    public void saveManager(Manager manager) {
        managerRepository.save(manager);
    }

    public SchoolNurse findNurseByUsername(String username) {
        return schoolNurseRepository.findByUser_Username(username).orElse(null);
    }

    public void saveNurse(SchoolNurse nurse) {
        schoolNurseRepository.save(nurse);
    }


    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student findStudentById(long id) {
        return studentRepository.findById(id).orElse(null);
    }

    // Lấy Parent hiện đang đăng nhập từ Spring Security
    public Parent getCurrentParent() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();  // hoặc username nếu không dùng email

        return parentRepository.findByUser_Username(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.PARENT_NOT_EXISTS));
    }



    // Lấy danh sách học sinh theo phụ huynh
    public List<Student> getStudentsByParent(Parent parent) {
        return studentRepository.findByParent(parent);
    }

    // Lấy danh sách học sinh theo id phụ huynh
    public List<Student> getStudentsByParentId(Long parentId) {
        return studentRepository.findByParent_Id(parentId);
    }

    public List<Student> getStudentsByParent() {
        Parent parent = getCurrentParent(); // lấy đúng parent entity
        return studentRepository.findByParent_Id(parent.getId()); // dùng parent_id
    }



    public boolean existsUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    public UserDTO findUserByEmail(String email) {
        return userMapper.toUserDTO(userRepository.findByEmail(email));
    }


//    reset password
    public void resetPassword(long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXISTS));
        user.setPassword(passwordEncoder.encode(newPassword));
        try{
            userRepository.save(user);
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }
    }
}
