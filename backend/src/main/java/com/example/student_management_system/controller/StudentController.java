package com.example.student_management_system.controller;

import com.example.student_management_system.model.Student;
import com.example.student_management_system.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Student Management", description = "APIs for managing student records including CRUD operations and marks updates")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/")
    @Operation(
        summary = "Test Endpoint",
        description = "Simple test endpoint to verify the API is running"
    )
    @ApiResponse(
        responseCode = "200",
        description = "API is running",
        content = @Content(mediaType = "text/plain")
    )
    public String show(){
        return "Hello World";
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping
    @Operation(
        summary = "Get All Students",
        description = "Retrieve a list of all students in the system"
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of students retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Student.class))
    )
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/{id}")
    @Operation(
        summary = "Get Student by ID",
        description = "Retrieve a specific student by their unique ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Student found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Student.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Student not found"
        )
    })
    public ResponseEntity<Student> getStudentById(
        @Parameter(description = "Student ID", required = true)
        @PathVariable String id
    ) {
        return studentService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping
    @Operation(
        summary = "Add New Student",
        description = "Create a new student record in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Student created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Student.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid student data"
        )
    })
    public Student addStudent(@RequestBody Student student) {
        return studentService.addStudent(student);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PutMapping("/{id}")
    @Operation(
        summary = "Update Student",
        description = "Update all details of an existing student by ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Student updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Student.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Student not found"
        )
    })
    public ResponseEntity<Student> updateStudent(
        @Parameter(description = "Student ID", required = true)
        @PathVariable String id,
        @RequestBody Student studentDetails
    ) {
        try {
            Student updatedStudent = studentService.updateStudent(id, studentDetails);
            return ResponseEntity.ok(updatedStudent);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @DeleteMapping("/email/{email}")
    @Operation(
        summary = "Delete Student by Email",
        description = "Remove a student record from the system using their email address"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Student deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Student not found"
        )
    })
    public ResponseEntity<Void> deleteStudent(
        @Parameter(description = "Student email address", required = true)
        @PathVariable String email
    ) {
        try {
            studentService.deleteStudent(email);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PatchMapping("/{id}/marks")
    @Operation(
        summary = "Update Student Marks",
        description = "Update only the marks field for a specific student"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Marks updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Student.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid marks data"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Student not found"
        )
    })
    public ResponseEntity<Student> updateMarks(
        @Parameter(description = "Student ID", required = true)
        @PathVariable String id,
        @RequestBody UpdateMarksRequest request
    ) {
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