package com.sms.student_mgmt_api;

import com.sms.model.Student;
import com.sms.service.StudentService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.sms")
@EnableJpaRepositories(basePackages = "com.sms.repository")
@EntityScan(basePackages = "com.sms.model")           // ← ADD THIS
public class StudentMgmtApiApplication implements CommandLineRunner {

    private final StudentService studentService;

    public StudentMgmtApiApplication(StudentService studentService) {
        this.studentService = studentService;
    }

    public static void main(String[] args) {
        SpringApplication.run(StudentMgmtApiApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("\n========== Saving students to MySQL ==========");

        Student alice   = new Student(null, "Alice",   20, "alice@sms.com",   "Computer Science");
        Student bob     = new Student(null, "Bob",     22, "bob@sms.com",     "Mathematics");
        Student charlie = new Student(null, "Charlie", 21, "charlie@sms.com", "Physics");

        studentService.addStudent(alice);
        studentService.addStudent(bob);
        studentService.addStudent(charlie);

        System.out.println("\n========== All students from DB ==========");
        studentService.getAllStudents().forEach(System.out::println);
        System.out.println("==========================================\n");
    }
}