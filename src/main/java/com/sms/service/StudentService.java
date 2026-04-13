package com.sms.service;

import com.sms.dto.StudentRequestDTO;
import com.sms.dto.StudentResponseDTO;
import com.sms.exception.StudentNotFoundException;
import com.sms.model.Student;
import com.sms.repository.StudentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

    public StudentService(StudentRepository studentRepository,
                          ModelMapper modelMapper) {
        this.studentRepository = studentRepository;
        this.modelMapper = modelMapper;
    }


    private StudentResponseDTO toResponse(Student student) {
        return modelMapper.map(student, StudentResponseDTO.class);
    }


    private Student toEntity(StudentRequestDTO dto) {
        return modelMapper.map(dto, Student.class);
    }


    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> getAllStudentsPaged(Pageable pageable) {
        return studentRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        return toResponse(student);
    }

    @Transactional
    public StudentResponseDTO addStudent(StudentRequestDTO dto) {
        Student student = toEntity(dto);
        Student saved = studentRepository.save(student);
        return toResponse(saved);
    }

    @Transactional
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO dto) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));

        existing.setName(dto.getName());
        existing.setAge(dto.getAge());
        existing.setEmail(dto.getEmail());
        existing.setDepartment(dto.getDepartment());

        return toResponse(studentRepository.save(existing));
    }

    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }
        studentRepository.deleteById(id);
    }
}