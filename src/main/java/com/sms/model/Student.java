package com.sms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                   // generates getters, setters, equals, hashCode, toString
@NoArgsConstructor      // generates no-arg constructor
@AllArgsConstructor     // generates all-args constructor
public class Student {

    private Long id;
    private String name;
    private int age;
    private String email;
    private String department;
}