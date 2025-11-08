package com.example.student_management_system.controller;

import com.example.student_management_system.model.Student;
import com.example.student_management_system.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import com.example.student_management_system.dto.UpdateMarksRequest;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/")
    public String show(){
        return "Hello World";
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable String id) {
        return studentService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping
    public Student addStudent(@RequestBody Student student) {
        return studentService.addStudent(student);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable String id, @RequestBody Student studentDetails) {
        try {
            Student updatedStudent = studentService.updateStudent(id, studentDetails);
            return ResponseEntity.ok(updatedStudent);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @DeleteMapping("/email/{email}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String email) {
        try {
            studentService.deleteStudent(email);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PatchMapping("/{id}/marks")
    public ResponseEntity<Student> updateMarks(@PathVariable String id, @RequestBody UpdateMarksRequest request) {
        if (request == null || request.getMarks() == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Student updated = studentService.updateMarks(id, request.getMarks());
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}