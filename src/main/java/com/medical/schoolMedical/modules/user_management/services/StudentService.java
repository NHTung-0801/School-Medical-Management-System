package com.medical.schoolMedical.modules.user_management.services;

import com.medical.schoolMedical.enums.Gender;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.exceptions.ErrorCode;
import com.medical.schoolMedical.modules.user_management.dto.StudentDTO;
import com.medical.schoolMedical.modules.user_management.entities.Parent;
import com.medical.schoolMedical.modules.user_management.entities.Student;
import com.medical.schoolMedical.modules.user_management.mappers.StudentMapper;
import com.medical.schoolMedical.modules.user_management.repositories.ParentRepository;
import com.medical.schoolMedical.modules.user_management.repositories.StudentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentService {
    StudentRepository studentRepository;
    StudentMapper studentMapper;
    ParentRepository parentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<StudentDTO> getAllStudentsDTO() {
        return studentMapper.toStudentDTOs(studentRepository.findAll());
    }

    public List<Student> getByParentId(Long parentId) {
        return studentRepository.findByParent_Id(parentId);
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));
    }

    public void createStudent(String fullName, Gender gender, LocalDate birthDate,
                              String address, String className, Long parentId) {

        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PARENT_NOT_EXISTS));

        Student student = new Student();
        student.setFullName(fullName);
        student.setGender(gender);
        student.setBirthDate(birthDate);
        student.setAddress(address);
        student.setClassName(className);
        student.setParent(parent);

        studentRepository.save(student);
    }

    public StudentDTO getStudentById_DTO(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));

        return studentMapper.toStudentDTO(student);
    }

    // Thống kê học sinh được tạo theo tháng và năm
    public List<StudentDTO> getStudentsCreatedThisMonth() {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();
        return studentMapper.toStudentDTOs(studentRepository.findByMonthAndYear(currentMonth, currentYear));
    }

    public List<StudentDTO> getStudentsCreatedLastMonth() {
        LocalDate now = LocalDate.now();
        LocalDate lastMonth = now.minusMonths(1);

        int month = lastMonth.getMonthValue();
        int year = lastMonth.getYear();
        log.info("Fetching students created in month: {}, year: {}", month, year);
        List<Student> students = studentRepository.findByMonthAndYear(month, year);
        log.info("Students created last month: {}", students);
        return studentMapper.toStudentDTOs(students);
    }
}
