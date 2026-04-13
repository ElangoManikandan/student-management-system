package com.sms.controller;

import com.sms.dto.CourseRequestDTO;
import com.sms.dto.CourseResponseDTO;
import com.sms.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }


    @PostMapping
    public ResponseEntity<CourseResponseDTO> createCourse(
            @Valid @RequestBody CourseRequestDTO dto) {
        CourseResponseDTO saved = courseService.createCourse(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    @GetMapping
    public ResponseEntity<List<CourseResponseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/students-by-course")
    public ResponseEntity<List<String>> getStudentsByCourse(@RequestParam String title) {
        return ResponseEntity.ok(courseService.findStudentsByCourseTitle(title));
    }

    @GetMapping("/students-with-many-courses")
    public ResponseEntity<List<String>> getStudentsWithManyCourses(@RequestParam int count) {
        return ResponseEntity.ok(courseService.findStudentsWithMoreThanNCourses(count));
    }
}