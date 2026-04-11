package com.sms.repository;

import com.sms.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    // JpaRepository<Entity, PrimaryKeyType>
    //
    // Built-in methods you get for free:
    //   save(student)          → INSERT or UPDATE
    //   findById(id)           → Optional<Student>
    //   findAll()              → List<Student>
    //   deleteById(id)         → DELETE WHERE id = ?
    //   count()                → SELECT COUNT(*)
    //   existsById(id)         → boolean
    //
    // Day 7 — you will add custom @Query methods here
}