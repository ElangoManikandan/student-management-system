package com.sms;

import com.sms.model.Student;
import com.sms.service.StudentService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudentMgmtApiApplication implements CommandLineRunner {

    private final StudentService studentService;

    public StudentMgmtApiApplication(StudentService studentService) {
        this.studentService = studentService;
    }

    public static void main(String[] args) {
        SpringApplication.run(StudentMgmtApiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        // T7 — Add 3 students via service and print all
        System.out.println("\n========== Seeding students ==========");

        studentService.addStudent(
                new Student(null, "Alice", 20, "alice@sms.com", "Computer Science"));
        studentService.addStudent(
                new Student(null, "Bob", 22, "bob@sms.com", "Mathematics"));
        studentService.addStudent(
                new Student(null, "Charlie", 21, "charlie@sms.com", "Physics"));

        System.out.println("\n========== All students ==========");
        studentService.getAllStudents()
                .forEach(System.out::println);
        System.out.println("===================================\n");
    }
}