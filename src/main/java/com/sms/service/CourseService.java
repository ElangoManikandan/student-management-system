package com.sms.service;

import com.sms.dto.CourseRequestDTO;
import com.sms.dto.CourseResponseDTO;
import com.sms.exception.StudentNotFoundException;
import com.sms.model.Course;
import com.sms.model.Student;
import com.sms.repository.CourseRepository;
import com.sms.repository.StudentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

    public CourseService(CourseRepository courseRepository,
                         StudentRepository studentRepository,
                         ModelMapper modelMapper) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.modelMapper = modelMapper;
    }

    // ── T4: Create course ─────────────────────────────────────────────
    @Transactional
    public CourseResponseDTO createCourse(CourseRequestDTO dto) {
        Course course = modelMapper.map(dto, Course.class);
        Course saved = courseRepository.save(course);
        return modelMapper.map(saved, CourseResponseDTO.class);
    }

    // ── T4: Get all courses ───────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(c -> modelMapper.map(c, CourseResponseDTO.class))
                .collect(Collectors.toList());
    }

    // ── T6: Enroll a student into a course ────────────────────────────
    @Transactional
    public String enrollStudent(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found with id: " + courseId));

        student.getCourses().add(course);    // adds row to student_course table
        course.getStudents().add(student);   // keeps both sides in sync

        studentRepository.save(student);

        return student.getName() + " enrolled in " + course.getTitle();
    }

    // ── T7: Find students by course title ─────────────────────────────
    @Transactional(readOnly = true)
    public List<String> findStudentsByCourseTitle(String title) {
        return studentRepository.findStudentsByCourseTitle(title)
                .stream()
                .map(Student::getName)
                .collect(Collectors.toList());
    }

    // ── T8: Find students enrolled in more than N courses ─────────────
    @Transactional(readOnly = true)
    public List<String> findStudentsWithMoreThanNCourses(int count) {
        return studentRepository.findStudentsWithMoreThanNCourses(count)
                .stream()
                .map(s -> s.getName() + " (" + s.getCourses().size() + " courses)")
                .collect(Collectors.toList());
    }
}