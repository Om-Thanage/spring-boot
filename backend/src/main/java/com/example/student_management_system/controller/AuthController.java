package com.example.student_management_system.controller;

import com.example.student_management_system.dto.LoginRequest;
import com.example.student_management_system.dto.LoginResponse;
import com.example.student_management_system.model.Admin;
import com.example.student_management_system.repository.AdminRepository;
import com.example.student_management_system.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Authentication", description = "Authentication management APIs for admin login, registration and token verification")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/login")
    @Operation(
        summary = "Admin Login",
        description = "Authenticate admin user with email and password. Returns JWT token for subsequent API calls."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(mediaType = "text/plain")
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "text/plain")
        )
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            Admin admin = adminRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

            String token = jwtUtil.generateToken(admin.getEmail());
            
            return ResponseEntity.ok(new LoginResponse(token, admin.getEmail(), admin.getName()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/register")
    @Operation(
        summary = "Register Admin",
        description = "Register a new admin user. Email must be unique. Password will be encrypted before storage."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Admin registered successfully",
            content = @Content(mediaType = "text/plain")
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Email already exists",
            content = @Content(mediaType = "text/plain")
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "text/plain")
        )
    })
    public ResponseEntity<?> register(@RequestBody Admin admin) {
        try {
            if (adminRepository.findByEmail(admin.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists");
            }

            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            Admin savedAdmin = adminRepository.save(admin);
            
            return ResponseEntity.status(HttpStatus.CREATED).body("Admin registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/verify")
    @Operation(
        summary = "Verify JWT Token",
        description = "Verify if the provided JWT token is valid and not expired. Returns admin details if valid."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Token is valid",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid or expired token",
            content = @Content(mediaType = "text/plain")
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> verifyToken(
        @Parameter(description = "JWT token with Bearer prefix", required = true)
        @RequestHeader("Authorization") String token
    ) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwt = token.substring(7);
                String email = jwtUtil.extractEmail(jwt);
                
                if (!jwtUtil.isTokenExpired(jwt)) {
                    Admin admin = adminRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Admin not found"));
                    return ResponseEntity.ok(new LoginResponse(jwt, admin.getEmail(), admin.getName()));
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}
