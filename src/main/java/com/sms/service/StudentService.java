package com.sms.service;

import com.sms.model.Student;
import com.sms.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

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

    public Student updateStudent(Long id, Student updatedData) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Student not found with id: " + id));


        existing.setName(updatedData.getName());
        existing.setAge(updatedData.getAge());
        existing.setEmail(updatedData.getEmail());
        existing.setDepartment(updatedData.getDepartment());

        return studentRepository.save(existing);   // UPDATE query
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}