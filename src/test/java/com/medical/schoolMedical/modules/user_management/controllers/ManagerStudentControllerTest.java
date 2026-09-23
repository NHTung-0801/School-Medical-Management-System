package com.medical.schoolMedical.modules.user_management.controllers;

import com.medical.schoolMedical.enums.Gender;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.modules.user_management.entities.Parent;
import com.medical.schoolMedical.modules.user_management.entities.Student;
import com.medical.schoolMedical.modules.user_management.entities.User;
import com.medical.schoolMedical.modules.user_management.services.ParentService;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ManagerStudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParentService parentService;

    @MockBean
    private StudentService studentService;

    @Test
    @WithMockUser(username = "managerUser", roles = {"MANAGER"})
    @DisplayName("GET /manager/students/list should return student_list view with studentList attribute")
    void testListStudents() throws Exception {
        User parentUser = new User();
        parentUser.setUsername("0912345678");

        Parent parent = new Parent();
        parent.setId(10L);
        parent.setFullName("Phụ huynh An");
        parent.setUser(parentUser);

        Student s = new Student();
        s.setId(1L);
        s.setFullName("Nguyễn Văn An");
        s.setClassName("10A1");
        s.setGender(Gender.MALE);
        s.setBirthDate(LocalDate.of(2010, 1, 1));
        s.setAddress("Hà Nội");
        s.setParent(parent);

        when(studentService.getAllStudents()).thenReturn(List.of(s));

        mockMvc.perform(get("/manager/students/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("manager/student_list"))
                .andExpect(model().attributeExists("studentList"));

        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    @WithMockUser(username = "managerUser", roles = {"MANAGER"})
    @DisplayName("GET /manager/students/create should return student_form with parentList")
    void testShowCreateStudentForm() throws Exception {
        Parent p = new Parent();
        p.setId(10L);
        p.setFullName("Phụ huynh Bình");

        when(parentService.getAllParents()).thenReturn(List.of(p));

        mockMvc.perform(get("/manager/students/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("manager/student_form"))
                .andExpect(model().attribute("isEdit", false))
                .andExpect(model().attributeExists("parentList"));

        verify(parentService, times(1)).getAllParents();
    }

    @Test
    @WithMockUser(username = "managerUser", roles = {"MANAGER"})
    @DisplayName("POST /manager/students/create with valid data should succeed and redirect")
    void testCreateStudent_Success() throws Exception {
        mockMvc.perform(post("/manager/students/create")
                        .with(csrf())
                        .param("fullName", "Lê Thị Thảo")
                        .param("gender", "FEMALE")
                        .param("birthDate", "2010-05-15")
                        .param("address", "Hà Nội")
                        .param("className", "10A2")
                        .param("parentId", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/students/list"))
                .andExpect(flash().attributeExists("success"));

        verify(studentService, times(1)).createStudent(
                eq("Lê Thị Thảo"),
                eq(Gender.FEMALE),
                eq(LocalDate.of(2010, 5, 15)),
                eq("Hà Nội"),
                eq("10A2"),
                eq(10L)
        );
    }

    @Test
    @WithMockUser(username = "managerUser", roles = {"MANAGER"})
    @DisplayName("POST /manager/students/create with invalid name containing special characters should fail validation")
    void testCreateStudent_InvalidName() throws Exception {
        mockMvc.perform(post("/manager/students/create")
                        .with(csrf())
                        .param("fullName", "123456")
                        .param("gender", "MALE")
                        .param("birthDate", "2010-05-15")
                        .param("address", "Hà Nội")
                        .param("className", "10A2")
                        .param("parentId", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/students/create"))
                .andExpect(flash().attributeExists("error"));

        verify(studentService, never()).createStudent(anyString(), any(), any(), anyString(), anyString(), anyLong());
    }

    @Test
    @WithMockUser(username = "managerUser", roles = {"MANAGER"})
    @DisplayName("GET /manager/students/edit/{id} with existing student should return form with isEdit=true")
    void testShowEditStudentForm_Success() throws Exception {
        Student s = new Student();
        s.setId(1L);
        s.setFullName("Nguyễn Văn An");

        when(studentService.getStudentById(1L)).thenReturn(s);
        when(parentService.getAllParents()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/manager/students/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("manager/student_form"))
                .andExpect(model().attribute("isEdit", true))
                .andExpect(model().attributeExists("student"));
    }

    @Test
    @WithMockUser(username = "managerUser", roles = {"MANAGER"})
    @DisplayName("POST /manager/students/delete/{id} should delete student successfully")
    void testDeleteStudent_Success() throws Exception {
        doNothing().when(studentService).deleteStudent(1L);

        mockMvc.perform(post("/manager/students/delete/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/students/list"))
                .andExpect(flash().attribute("success", "Xoá học sinh thành công!"));

        verify(studentService, times(1)).deleteStudent(1L);
    }

    @Test
    @WithMockUser(username = "managerUser", roles = {"MANAGER"})
    @DisplayName("POST /manager/students/delete/{id} when foreign key constraint fails should display friendly error")
    void testDeleteStudent_ForeignKeyConstraintError() throws Exception {
        doThrow(new RuntimeException("Data integrity violation")).when(studentService).deleteStudent(99L);

        mockMvc.perform(post("/manager/students/delete/99").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/students/list"))
                .andExpect(flash().attribute("error", "Không thể xoá học sinh do đang có hồ sơ sức khỏe hoặc dữ liệu y tế liên kết."));
    }
}
