package com.sms.student_mgmt_api;

import com.sms.dto.CourseRequestDTO;
import com.sms.dto.CourseResponseDTO;
import com.sms.exception.StudentNotFoundException;
import com.sms.model.Course;
import com.sms.model.Student;
import com.sms.repository.CourseRepository;
import com.sms.repository.StudentRepository;
import com.sms.service.CourseService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    CourseRepository courseRepository;

    @Mock
    StudentRepository studentRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    CourseService courseService;

    private Course course;
    private Student student;
    private CourseRequestDTO requestDTO;
    private CourseResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        course   = new Course(1L, "Math", 3, "6 months", new HashSet<>());
        student  = new Student(1L, "Alice", 20, "alice@sms.com", "CS", new HashSet<>());
        requestDTO  = new CourseRequestDTO("Math", 3, "6 months");
        responseDTO = new CourseResponseDTO(1L, "Math", 3, "6 months");
    }

    // ── T4: createCourse ──────────────────────────────────────────────

    @Test
    void createCourse_savesAndReturnsMappedDTO() {
        when(modelMapper.map(requestDTO, Course.class)).thenReturn(course);
        when(courseRepository.save(course)).thenReturn(course);
        when(modelMapper.map(course, CourseResponseDTO.class)).thenReturn(responseDTO);

        CourseResponseDTO result = courseService.createCourse(requestDTO);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Math");
        assertThat(result.getCredits()).isEqualTo(3);
        assertThat(result.getDuration()).isEqualTo("6 months");
        verify(courseRepository).save(course);
    }

    // ── T4: getAllCourses ─────────────────────────────────────────────

    @Test
    void getAllCourses_returnsMappedList() {
        Course course2 = new Course(2L, "Science", 4, "1 year", new HashSet<>());
        CourseResponseDTO responseDTO2 = new CourseResponseDTO(2L, "Science", 4, "1 year");

        when(courseRepository.findAll()).thenReturn(List.of(course, course2));
        when(modelMapper.map(course, CourseResponseDTO.class)).thenReturn(responseDTO);
        when(modelMapper.map(course2, CourseResponseDTO.class)).thenReturn(responseDTO2);

        List<CourseResponseDTO> result = courseService.getAllCourses();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CourseResponseDTO::getTitle)
                .containsExactlyInAnyOrder("Math", "Science");
    }

    @Test
    void getAllCourses_emptyRepository_returnsEmptyList() {
        when(courseRepository.findAll()).thenReturn(List.of());

        List<CourseResponseDTO> result = courseService.getAllCourses();

        assertThat(result).isEmpty();
        verify(courseRepository).findAll();
    }

    // ── T6: enrollStudent ─────────────────────────────────────────────

    @Test
    void enrollStudent_success_returnsConfirmationMessage() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(studentRepository.save(student)).thenReturn(student);

        String result = courseService.enrollStudent(1L, 1L);

        assertThat(result).isEqualTo("Alice enrolled in Math");
        assertThat(student.getCourses()).contains(course);
        assertThat(course.getStudents()).contains(student);
        verify(studentRepository).save(student);
    }

    @Test
    void enrollStudent_studentNotFound_throwsStudentNotFoundException() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.enrollStudent(99L, 1L))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("99");

        verify(courseRepository, never()).findById(any());
    }

    @Test
    void enrollStudent_courseNotFound_throwsRuntimeException() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.enrollStudent(1L, 99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Course not found with id: 99");

        verify(studentRepository, never()).save(any());
    }

    @Test
    void enrollStudent_studentAlreadyEnrolled_doesNotDuplicateEntry() {
        // Pre-add the course — HashSet guarantees no duplicates
        student.getCourses().add(course);
        course.getStudents().add(student);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(studentRepository.save(student)).thenReturn(student);

        String result = courseService.enrollStudent(1L, 1L);

        assertThat(result).isEqualTo("Alice enrolled in Math");
        assertThat(student.getCourses()).hasSize(1);   // still one, no duplicate
    }

    // ── T7: findStudentsByCourseTitle ─────────────────────────────────

    @Test
    void findStudentsByCourseTitle_returnsStudentNames() {
        Student student2 = new Student(2L, "Bob", 22, "bob@sms.com", "EE", new HashSet<>());
        when(studentRepository.findStudentsByCourseTitle("Math"))
                .thenReturn(List.of(student, student2));

        List<String> result = courseService.findStudentsByCourseTitle("Math");

        assertThat(result).containsExactly("Alice", "Bob");
    }

    @Test
    void findStudentsByCourseTitle_noEnrollments_returnsEmptyList() {
        when(studentRepository.findStudentsByCourseTitle("Physics")).thenReturn(List.of());

        List<String> result = courseService.findStudentsByCourseTitle("Physics");

        assertThat(result).isEmpty();
    }

    // ── T8: findStudentsWithMoreThanNCourses ──────────────────────────

    @Test
    void findStudentsWithMoreThanNCourses_returnsFormattedNameWithCount() {
        Course c1 = new Course(1L, "Math", 3, "6 months", new HashSet<>());
        Course c2 = new Course(2L, "Science", 4, "1 year", new HashSet<>());
        student.getCourses().add(c1);
        student.getCourses().add(c2);

        when(studentRepository.findStudentsWithMoreThanNCourses(1)).thenReturn(List.of(student));

        List<String> result = courseService.findStudentsWithMoreThanNCourses(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).contains("Alice");
        assertThat(result.get(0)).contains("2 courses");
    }

    @Test
    void findStudentsWithMoreThanNCourses_noneQualify_returnsEmptyList() {
        when(studentRepository.findStudentsWithMoreThanNCourses(5)).thenReturn(List.of());

        List<String> result = courseService.findStudentsWithMoreThanNCourses(5);

        assertThat(result).isEmpty();
    }

    @Test
    void findStudentsWithMoreThanNCourses_multipleStudents_returnsAll() {
        Student student2 = new Student(2L, "Bob", 22, "bob@sms.com", "EE", new HashSet<>());
        Course c1 = new Course(1L, "Math", 3, "6 months", new HashSet<>());
        Course c2 = new Course(2L, "Science", 4, "1 year", new HashSet<>());
        Course c3 = new Course(3L, "English", 2, "3 months", new HashSet<>());
        student.getCourses().addAll(List.of(c1, c2));
        student2.getCourses().addAll(List.of(c1, c2, c3));

        when(studentRepository.findStudentsWithMoreThanNCourses(1))
                .thenReturn(List.of(student, student2));

        List<String> result = courseService.findStudentsWithMoreThanNCourses(1);

        assertThat(result).hasSize(2);
        assertThat(result.get(0)).contains("Alice").contains("2 courses");
        assertThat(result.get(1)).contains("Bob").contains("3 courses");
    }
}
