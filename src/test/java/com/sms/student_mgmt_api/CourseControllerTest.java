package com.sms.student_mgmt_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.controller.CourseController;
import com.sms.dto.CourseRequestDTO;
import com.sms.dto.CourseResponseDTO;
import com.sms.exception.StudentNotFoundException;
import com.sms.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
@WithMockUser
class CourseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CourseService courseService;

    private CourseResponseDTO courseResponseDTO;
    private CourseRequestDTO courseRequestDTO;

    @BeforeEach
    void setUp() {
        courseResponseDTO = new CourseResponseDTO(1L, "Math", 3, "6 months");
        courseRequestDTO  = new CourseRequestDTO("Math", 3, "6 months");
    }

    // ── POST /api/courses ─────────────────────────────────────────────

    @Test
    void createCourse_validBody_returnsCreated() throws Exception {
        when(courseService.createCourse(any())).thenReturn(courseResponseDTO);

        mockMvc.perform(post("/api/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Math"))
                .andExpect(jsonPath("$.credits").value(3))
                .andExpect(jsonPath("$.duration").value("6 months"));
    }

    @Test
    void createCourse_blankTitle_returns400() throws Exception {
        CourseRequestDTO invalid = new CourseRequestDTO("", 3, "6 months");

        mockMvc.perform(post("/api/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createCourse_zeroCredits_returns400() throws Exception {
        CourseRequestDTO invalid = new CourseRequestDTO("Math", 0, "6 months");

        mockMvc.perform(post("/api/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCourse_blankDuration_returns400() throws Exception {
        CourseRequestDTO invalid = new CourseRequestDTO("Math", 3, "");

        mockMvc.perform(post("/api/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCourse_nullCredits_returns400() throws Exception {
        CourseRequestDTO invalid = new CourseRequestDTO("Math", null, "6 months");

        mockMvc.perform(post("/api/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/courses ──────────────────────────────────────────────

    @Test
    void getAllCourses_returnsOkWithList() throws Exception {
        when(courseService.getAllCourses()).thenReturn(List.of(courseResponseDTO));

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Math"))
                .andExpect(jsonPath("$[0].credits").value(3));
    }

    @Test
    void getAllCourses_emptyList_returnsOk() throws Exception {
        when(courseService.getAllCourses()).thenReturn(List.of());

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ── GET /api/courses/students-by-course ───────────────────────────

    @Test
    void getStudentsByCourse_returnsMatchingNames() throws Exception {
        when(courseService.findStudentsByCourseTitle("Math")).thenReturn(List.of("Alice", "Bob"));

        mockMvc.perform(get("/api/courses/students-by-course")
                        .param("title", "Math"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Alice"))
                .andExpect(jsonPath("$[1]").value("Bob"));
    }

    @Test
    void getStudentsByCourse_noStudentsEnrolled_returnsEmptyList() throws Exception {
        when(courseService.findStudentsByCourseTitle("Physics")).thenReturn(List.of());

        mockMvc.perform(get("/api/courses/students-by-course")
                        .param("title", "Physics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ── GET /api/courses/students-with-many-courses ───────────────────

    @Test
    void getStudentsWithManyCourses_returnsFormattedNames() throws Exception {
        when(courseService.findStudentsWithMoreThanNCourses(2))
                .thenReturn(List.of("Alice (3 courses)"));

        mockMvc.perform(get("/api/courses/students-with-many-courses")
                        .param("count", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Alice (3 courses)"));
    }

    @Test
    void getStudentsWithManyCourses_noneQualify_returnsEmptyList() throws Exception {
        when(courseService.findStudentsWithMoreThanNCourses(10)).thenReturn(List.of());

        mockMvc.perform(get("/api/courses/students-with-many-courses")
                        .param("count", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ── Exception paths via GlobalExceptionHandler ────────────────────

    @Test
    void createCourse_serviceThrowsRuntimeException_returns500() throws Exception {
        when(courseService.createCourse(any()))
                .thenThrow(new RuntimeException("Unexpected DB error"));

        mockMvc.perform(post("/api/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseRequestDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
    }

    @Test
    void getStudentsByCourse_studentNotFound_returns404() throws Exception {
        when(courseService.findStudentsByCourseTitle("Unknown"))
                .thenThrow(new StudentNotFoundException("Student not found"));

        mockMvc.perform(get("/api/courses/students-by-course")
                        .param("title", "Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
