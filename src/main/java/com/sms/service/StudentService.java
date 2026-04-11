package com.sms.service;

import com.sms.model.Student;
import com.sms.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    // Constructor injection — preferred over @Autowired on field
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Student not found with id: " + id));
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}