package com.sms.repository;

import com.sms.model.Student;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class StudentRepository {

    // In-memory store — replaced by JpaRepository on Day 4
    private final List<Student> store = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Student save(Student student) {
        student.setId(idCounter.getAndIncrement());
        store.add(student);
        return student;
    }

    public List<Student> findAll() {
        return new ArrayList<>(store);
    }

    public Optional<Student> findById(Long id) {
        return store.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    public void deleteById(Long id) {
        store.removeIf(s -> s.getId().equals(id));
    }
}