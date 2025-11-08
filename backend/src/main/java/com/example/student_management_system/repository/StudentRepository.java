package com.example.student_management_system.repository;
import com.example.student_management_system.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentRepository extends MongoRepository<Student, String> {
	// Derived delete query by email field
	void deleteByEmail(String email);
}