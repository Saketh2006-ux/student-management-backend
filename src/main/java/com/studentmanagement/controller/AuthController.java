package com.studentmanagement.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studentmanagement.dto.LoginRequest;
import com.studentmanagement.dto.RegisterRequest;
import com.studentmanagement.entity.Teacher;
import com.studentmanagement.repository.TeacherRepository;
import com.studentmanagement.security.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            TeacherRepository teacherRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request) {

        String fullName = request.getFullName().trim();
        String email = request.getEmail().trim().toLowerCase();

        if (fullName.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Full name is required."));
        }

        if (teacherRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message",
                            "An account with this email already exists."
                    ));
        }

        Teacher teacher = new Teacher();
        teacher.setFullName(fullName);
        teacher.setEmail(email);
        teacher.setPassword(
                passwordEncoder.encode(request.getPassword()));

        Teacher savedTeacher = teacherRepository.save(teacher);
        String token = jwtService.generateToken(savedTeacher);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Registration successful.",
                        "token", token,
                        "teacherId", savedTeacher.getId(),
                        "fullName", savedTeacher.getFullName(),
                        "email", savedTeacher.getEmail()
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        Teacher teacher = teacherRepository.findByEmail(email)
                .orElse(null);

        if (teacher == null
                || !passwordEncoder.matches(
                        request.getPassword(),
                        teacher.getPassword())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message", "Invalid email or password."
                    ));
        }

        String token = jwtService.generateToken(teacher);

        return ResponseEntity.ok(Map.of(
                "message", "Login successful.",
                "token", token,
                "teacherId", teacher.getId(),
                "fullName", teacher.getFullName(),
                "email", teacher.getEmail()
        ));
    }
}