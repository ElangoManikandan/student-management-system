package com.sms.model;

public abstract class Person {

    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    // Every subclass MUST implement this
    public abstract String getRole();

    @Override
    public String toString() {
        return getName() + " | Role: " + getRole() + " | Age: " + getAge();
    }
}