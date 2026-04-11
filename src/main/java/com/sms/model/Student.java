package com.sms.model;

public class Student extends Person implements Printable {

    private Long id;
    private String email;
    private String department;

    // No-arg constructor
    public Student() {
        super("", 0);
    }

    // All-args constructor
    public Student(Long id, String name, int age,
                   String email, String department) {
        super(name, age);
        this.id = id;
        this.email = email;
        this.department = department;
    }

    // Convenience constructor (without id)
    public Student(String name, int age,
                   String email, String department) {
        super(name, age);
        this.email = email;
        this.department = department;
    }

    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public void printDetails() {
        System.out.println(
                "ID: " + id
                        + " | Name: " + getName()
                        + " | Age: " + getAge()
                        + " | Email: " + email
                        + " | Dept: " + department
                        + " | Role: " + getRole()
        );
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name=" + getName()
                + ", dept=" + department + "}";
    }
}