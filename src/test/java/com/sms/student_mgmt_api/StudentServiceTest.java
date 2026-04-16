package com.sms.student_mgmt_api;

import com.sms.dto.StudentRequestDTO;
import com.sms.dto.StudentResponseDTO;
import com.sms.exception.StudentNotFoundException;
import com.sms.model.Student;
import com.sms.repository.StudentRepository;
import com.sms.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)   // activates Mockito — no Spring context
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;   // fake — no real DB

    @Mock
    private ModelMapper modelMapper;               // fake mapper

    @InjectMocks
    private StudentService studentService;         // real service with mocks injected

    private Student student;
    private StudentResponseDTO responseDTO;
    private StudentRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        student = new Student(1L, "Alice", 20, "alice@sms.com", "CS", new HashSet<>());
        responseDTO = new StudentResponseDTO(1L, "Alice", 20, "alice@sms.com", "CS");
        requestDTO = new StudentRequestDTO("Alice", 20, "alice@sms.com", "CS");
    }

    // ── T3: getAllStudents returns list from repository ────────────────
    @Test
    void getAllStudents_returnsListOfStudents() {
        when(studentRepository.findAll()).thenReturn(List.of(student));
        when(modelMapper.map(student, StudentResponseDTO.class))
                .thenReturn(responseDTO);

        List<StudentResponseDTO> result = studentService.getAllStudents();

        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getName());
        verify(studentRepository, times(1)).findAll();
    }

    // ── T4: getStudentById throws when ID does not exist ──────────────
    @Test
    void getStudentById_nonExistentId_throwsStudentNotFoundException() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class,
                () -> studentService.getStudentById(99L));

        verify(studentRepository, times(1)).findById(99L);
    }

    // ── T4: getStudentById returns DTO when student exists ────────────
    @Test
    void getStudentById_validId_returnsStudentResponseDTO() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(modelMapper.map(student, StudentResponseDTO.class))
                .thenReturn(responseDTO);

        StudentResponseDTO result = studentService.getStudentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Alice", result.getName());
    }

    // ── T5: addStudent saves entity and returns DTO ───────────────────
    @Test
    void addStudent_validRequest_savesAndReturnsDTO() {
        when(modelMapper.map(requestDTO, Student.class)).thenReturn(student);
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(modelMapper.map(student, StudentResponseDTO.class))
                .thenReturn(responseDTO);

        StudentResponseDTO result = studentService.addStudent(requestDTO);

        assertNotNull(result);
        assertEquals("alice@sms.com", result.getEmail());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    // ── T5: addStudent calls save exactly once ────────────────────────
    @Test
    void addStudent_callsRepositorySaveOnce() {
        when(modelMapper.map(requestDTO, Student.class)).thenReturn(student);
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(modelMapper.map(student, StudentResponseDTO.class))
                .thenReturn(responseDTO);

        studentService.addStudent(requestDTO);

        verify(studentRepository, times(1)).save(student);
    }

    // ── deleteStudent throws when student not found ───────────────────
    @Test
    void deleteStudent_nonExistentId_throwsStudentNotFoundException() {
        when(studentRepository.existsById(99L)).thenReturn(false);

        assertThrows(StudentNotFoundException.class,
                () -> studentService.deleteStudent(99L));

        verify(studentRepository, never()).deleteById(any());
    }
}