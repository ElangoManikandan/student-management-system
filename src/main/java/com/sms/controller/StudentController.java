package com.sms.controller;

import com.sms.dto.StudentRequestDTO;
import com.sms.dto.StudentResponseDTO;
import com.sms.service.CourseService;
import com.sms.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;
    private final CourseService courseService;

    public StudentController(StudentService studentService, CourseService courseService) {
        this.studentService = studentService;
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<StudentResponseDTO>> getAllStudentsPaged(
            Pageable pageable) {
        return ResponseEntity.ok(studentService.getAllStudentsPaged(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getStudentById(
            @PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @PostMapping
    public ResponseEntity<StudentResponseDTO> createStudent(
            @Valid @RequestBody StudentRequestDTO dto) {
        StudentResponseDTO saved = studentService.addStudent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequestDTO dto) {
        return ResponseEntity.ok(studentService.updateStudent(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    // ADD this method inside the existing StudentController class
// (below the existing deleteStudent method)

    // T6: POST /api/students/{studentId}/enroll/{courseId}  →  200 OK
    @PostMapping("/{studentId}/enroll/{courseId}")
    public ResponseEntity<String> enrollStudentInCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        String result = courseService.enrollStudent(studentId, courseId);
        return ResponseEntity.ok(result);
    }

// Also inject CourseService in the constructor:
// private final CourseService courseService;
// Add courseService as a constructor parameter alongside studentService
}