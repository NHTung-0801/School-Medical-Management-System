package com.medical.schoolMedical.modules.user_management.services;

import com.medical.schoolMedical.enums.Gender;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.exceptions.ErrorCode;
import com.medical.schoolMedical.modules.user_management.entities.Parent;
import com.medical.schoolMedical.modules.user_management.entities.Student;
import com.medical.schoolMedical.modules.user_management.mappers.StudentMapper;
import com.medical.schoolMedical.modules.user_management.repositories.ParentRepository;
import com.medical.schoolMedical.modules.user_management.repositories.StudentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private StudentMapper studentMapper;
    @Mock
    private ParentRepository parentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    @DisplayName("Should create student successfully when parent exists")
    void testCreateStudentSuccess() {
        Parent parent = new Parent();
        parent.setId(1L);
        parent.setFullName("Nguyen Van A (Parent)");

        when(parentRepository.findById(1L)).thenReturn(Optional.of(parent));

        studentService.createStudent(
                "Nguyen Van B",
                Gender.MALE,
                LocalDate.of(2015, 5, 20),
                "123 Duong Le Loi",
                "Class 3A",
                1L
        );

        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when creating student with non-existent parent")
    void testCreateStudentParentNotFound() {
        when(parentRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            studentService.createStudent(
                    "Nguyen Van B",
                    Gender.MALE,
                    LocalDate.of(2015, 5, 20),
                    "123 Duong Le Loi",
                    "Class 3A",
                    999L
            );
        });

        assertEquals(ErrorCode.PARENT_NOT_EXISTS, exception.getErrorCode());
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should retrieve student by ID when found")
    void testGetStudentByIdSuccess() {
        Student student = new Student();
        student.setId(5L);
        student.setFullName("Tran Thi C");

        when(studentRepository.findById(5L)).thenReturn(Optional.of(student));

        Student result = studentService.getStudentById(5L);

        assertNotNull(result);
        assertEquals("Tran Thi C", result.getFullName());
    }

    @Test
    @DisplayName("Should throw BusinessException when student ID not found")
    void testGetStudentByIdNotFound() {
        when(studentRepository.findById(100L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            studentService.getStudentById(100L);
        });

        assertEquals(ErrorCode.STUDENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should update student successfully when student and parent exist")
    void testUpdateStudentSuccess() {
        Student student = new Student();
        student.setId(10L);
        student.setFullName("Old Name");

        Parent parent = new Parent();
        parent.setId(2L);
        parent.setFullName("Parent Name");

        when(studentRepository.findById(10L)).thenReturn(Optional.of(student));
        when(parentRepository.findById(2L)).thenReturn(Optional.of(parent));

        studentService.updateStudent(10L, "New Name", Gender.FEMALE, LocalDate.of(2016, 8, 15), "New Address", "4B", 2L);

        assertEquals("New Name", student.getFullName());
        assertEquals(Gender.FEMALE, student.getGender());
        assertEquals("4B", student.getClassName());
        assertEquals(parent, student.getParent());
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    @DisplayName("Should throw BusinessException when updating non-existent student")
    void testUpdateStudentNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            studentService.updateStudent(99L, "New Name", Gender.FEMALE, LocalDate.of(2016, 8, 15), "New Address", "4B", 2L);
        });

        assertEquals(ErrorCode.STUDENT_NOT_FOUND, exception.getErrorCode());
        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete student successfully when student exists")
    void testDeleteStudentSuccess() {
        Student student = new Student();
        student.setId(15L);

        when(studentRepository.findById(15L)).thenReturn(Optional.of(student));

        studentService.deleteStudent(15L);

        verify(studentRepository, times(1)).delete(student);
    }

    @Test
    @DisplayName("Should throw BusinessException when deleting non-existent student")
    void testDeleteStudentNotFound() {
        when(studentRepository.findById(88L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            studentService.deleteStudent(88L);
        });

        assertEquals(ErrorCode.STUDENT_NOT_FOUND, exception.getErrorCode());
        verify(studentRepository, never()).delete(any());
    }
}
