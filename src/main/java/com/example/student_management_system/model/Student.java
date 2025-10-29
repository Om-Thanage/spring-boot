package com.example.student_management_system.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "students")
@Data
public class Student {
    @Id
    private String id; // MongoDB ObjectId as hex string
    private String name;
    private String email;
    private String course;
    private Double marks;
}
