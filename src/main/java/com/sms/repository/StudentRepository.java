package com.sms.repository;

import com.sms.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // T7 — Find all students enrolled in a course with a given title
    // JOIN FETCH avoids the N+1 problem by loading courses in one query
    @Query("SELECT DISTINCT s FROM Student s " +
            "JOIN s.courses c " +
            "WHERE LOWER(c.title) = LOWER(:title)")
    List<Student> findStudentsByCourseTitle(@Param("title") String title);

    // T8 — Find students enrolled in more than N courses
    @Query("SELECT s FROM Student s " +
            "WHERE SIZE(s.courses) > :count")
    List<Student> findStudentsWithMoreThanNCourses(@Param("count") int count);
}