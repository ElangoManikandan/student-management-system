package com.sms.student_mgmt_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.controller.StudentController;
import com.sms.dto.StudentRequestDTO;
import com.sms.dto.StudentResponseDTO;
import com.sms.exception.StudentNotFoundException;
import com.sms.service.CourseService;
import com.sms.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@WithMockUser
class StudentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    StudentService studentService;

    @MockBean
    CourseService courseService;

    private StudentResponseDTO responseDTO;
    private StudentRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new StudentResponseDTO(1L, "Alice", 20, "alice@sms.com", "CS");
        requestDTO  = new StudentRequestDTO("Alice", 20, "alice@sms.com", "CS");
    }

    // ── GET /api/students ─────────────────────────────────────────────

    @Test
    void getAllStudents_returnsOkWithList() throws Exception {
        when(studentService.getAllStudents()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[0].email").value("alice@sms.com"));
    }

    @Test
    void getAllStudents_emptyList_returnsOk() throws Exception {
        when(studentService.getAllStudents()).thenReturn(List.of());

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ── GET /api/students/paged ───────────────────────────────────────

    @Test
    void getAllStudentsPaged_returnsPagedResult() throws Exception {
        when(studentService.getAllStudentsPaged(any()))
                .thenReturn(new PageImpl<>(List.of(responseDTO), PageRequest.of(0, 5), 1));

        mockMvc.perform(get("/api/students/paged?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Alice"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    // ── GET /api/students/{id} ────────────────────────────────────────

    @Test
    void getStudentById_found_returnsOk() throws Exception {
        when(studentService.getStudentById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.department").value("CS"));
    }

    @Test
    void getStudentById_notFound_returns404() throws Exception {
        when(studentService.getStudentById(99L))
                .thenThrow(new StudentNotFoundException(99L));

        mockMvc.perform(get("/api/students/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Student not found with id: 99"));
    }

    // ── POST /api/students ────────────────────────────────────────────

    @Test
    void createStudent_validBody_returnsCreated() throws Exception {
        when(studentService.addStudent(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@sms.com"));
    }

    @Test
    void createStudent_blankName_returns400WithValidationError() throws Exception {
        StudentRequestDTO invalid = new StudentRequestDTO("", 20, "alice@sms.com", "CS");

        mockMvc.perform(post("/api/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createStudent_invalidEmail_returns400() throws Exception {
        StudentRequestDTO invalid = new StudentRequestDTO("Alice", 20, "not-an-email", "CS");

        mockMvc.perform(post("/api/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createStudent_ageTooLow_returns400() throws Exception {
        StudentRequestDTO invalid = new StudentRequestDTO("Alice", 10, "alice@sms.com", "CS");

        mockMvc.perform(post("/api/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    // ── PUT /api/students/{id} ────────────────────────────────────────

    @Test
    void updateStudent_found_returnsOk() throws Exception {
        when(studentService.updateStudent(eq(1L), any())).thenReturn(responseDTO);

        mockMvc.perform(put("/api/students/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateStudent_notFound_returns404() throws Exception {
        when(studentService.updateStudent(eq(99L), any()))
                .thenThrow(new StudentNotFoundException(99L));

        mockMvc.perform(put("/api/students/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStudent_invalidBody_returns400() throws Exception {
        StudentRequestDTO invalid = new StudentRequestDTO("A", 200, "bad-email", "");

        mockMvc.perform(put("/api/students/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    // ── DELETE /api/students/{id} ─────────────────────────────────────

    @Test
    void deleteStudent_found_returnsNoContent() throws Exception {
        doNothing().when(studentService).deleteStudent(1L);

        mockMvc.perform(delete("/api/students/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(studentService).deleteStudent(1L);
    }

    @Test
    void deleteStudent_notFound_returns404() throws Exception {
        doThrow(new StudentNotFoundException(99L)).when(studentService).deleteStudent(99L);

        mockMvc.perform(delete("/api/students/99").with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ── POST /api/students/{studentId}/enroll/{courseId} ─────────────

    @Test
    void enrollStudentInCourse_success_returnsMessage() throws Exception {
        when(courseService.enrollStudent(1L, 2L)).thenReturn("Alice enrolled in Math");

        mockMvc.perform(post("/api/students/1/enroll/2").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Alice enrolled in Math"));
    }

    @Test
    void enrollStudentInCourse_studentNotFound_returns404() throws Exception {
        when(courseService.enrollStudent(99L, 2L))
                .thenThrow(new StudentNotFoundException(99L));

        mockMvc.perform(post("/api/students/99/enroll/2").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
