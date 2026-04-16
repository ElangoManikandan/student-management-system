package com.sms.service;

import com.sms.dto.StudentRequestDTO;
import com.sms.dto.StudentResponseDTO;
import com.sms.exception.StudentNotFoundException;
import com.sms.model.Student;
import com.sms.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j                    // generates: private static final Logger log = ...
public class StudentService {

    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

    public StudentService(StudentRepository studentRepository,
                          ModelMapper modelMapper) {
        this.studentRepository = studentRepository;
        this.modelMapper = modelMapper;
    }

    private StudentResponseDTO toResponse(Student s) {
        return modelMapper.map(s, StudentResponseDTO.class);
    }

    private Student toEntity(StudentRequestDTO dto) {
        return modelMapper.map(dto, Student.class);
    }

    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getAllStudents() {
        log.info("Fetching all students");
        List<StudentResponseDTO> students = studentRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
        log.info("Returned {} students", students.size());
        return students;
    }

    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> getAllStudentsPaged(Pageable pageable) {
        log.info("Fetching students - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return studentRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentById(Long id) {
        log.info("Fetching student with id: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Student not found with id: {}", id);
                    return new StudentNotFoundException(id);
                });
        return toResponse(student);
    }

    @Transactional
    public StudentResponseDTO addStudent(StudentRequestDTO dto) {
        log.info("Creating new student with email: {}", dto.getEmail());
        Student saved = studentRepository.save(toEntity(dto));
        log.info("Student created successfully with id: {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO dto) {
        log.info("Updating student with id: {}", id);
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update failed — student not found with id: {}", id);
                    return new StudentNotFoundException(id);
                });
        existing.setName(dto.getName());
        existing.setAge(dto.getAge());
        existing.setEmail(dto.getEmail());
        existing.setDepartment(dto.getDepartment());
        log.info("Student with id: {} updated successfully", id);
        return toResponse(studentRepository.save(existing));
    }

    @Transactional
    public void deleteStudent(Long id) {
        log.info("Deleting student with id: {}", id);
        if (!studentRepository.existsById(id)) {
            log.warn("Delete failed — student not found with id: {}", id);
            throw new StudentNotFoundException(id);
        }
        studentRepository.deleteById(id);
        log.info("Student with id: {} deleted successfully", id);
    }
}