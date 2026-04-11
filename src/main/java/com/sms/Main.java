package com.sms;

import com.sms.exception.StudentNotFoundException;
import com.sms.model.GraduateStudent;
import com.sms.model.Person;
import com.sms.model.Student;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        System.out.println("=== T6: ArrayList of Students ===");

        List<Student> students = new ArrayList<>();
        students.add(new Student(1L, "Charlie", 21, "charlie@sms.com", "CS"));
        students.add(new Student(2L, "Alice",   20, "alice@sms.com",   "Math"));
        students.add(new Student(3L, "Eve",     22, "eve@sms.com",     "Physics"));
        students.add(new Student(4L, "Bob",     23, "bob@sms.com",     "CS"));
        students.add(new Student(5L, "Diana",   20, "diana@sms.com",   "Biology"));

        System.out.println("-- Before sorting --");
        students.forEach(Student::printDetails);

        System.out.println(" === T7: Sorted by Name ===");
        students.sort(Comparator.comparing(Student::getName));

        System.out.println("-- After sorting alphabetically --");
        students.forEach(Student::printDetails);

        System.out.println(" === T8: Exception Handling ===");

        Map<Long, Student> db = new HashMap<>();
        students.forEach(s -> db.put(s.getId(), s));

        // Valid lookup
        try {
            Student found = findById(db, 1L);
            System.out.println("Found: " + found);
        } catch (StudentNotFoundException e) {
            System.err.println(e.getMessage());
        }

        // Invalid lookup — throws exception
        try {
            Student notFound = findById(db, 99L);
            System.out.println("Found: " + notFound);
        } catch (StudentNotFoundException e) {
            System.err.println("Caught exception: " + e.getMessage());
        }

        System.out.println(" === BONUS: Polymorphism demo === ");

        List<Person> people = new ArrayList<>();
        people.add(new Student(6L, "Frank", 22, "frank@sms.com", "CS"));
        people.add(new GraduateStudent(7L, "Grace", 26, "grace@sms.com",
                "CS", "Machine Learning"));

        // Parent reference calls the overridden getRole() at runtime
        for (Person p : people) {
            System.out.println(p.getName() + " -> " + p.getRole());
        }
    }


    private static Student findById(Map<Long, Student> db, Long id) {
        Student s = db.get(id);
        if (s == null) {
            throw new StudentNotFoundException(id);
        }
        return s;
    }
}