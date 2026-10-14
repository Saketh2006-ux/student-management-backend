package com.studentmanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studentmanagement.entity.Student;
import com.studentmanagement.entity.Teacher;
import com.studentmanagement.repository.StudentRepository;
import com.studentmanagement.repository.TeacherRepository;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public StudentController(
            StudentRepository studentRepository,
            TeacherRepository teacherRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    private Teacher getAuthenticatedTeacher(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Authentication required");
        }

        return teacherRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new org.springframework.security.access.AccessDeniedException(
                                "Teacher account not found"));
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents(
            Authentication authentication) {

        Teacher teacher = getAuthenticatedTeacher(authentication);

        return ResponseEntity.ok(
                studentRepository.findAllByTeacherId(teacher.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(
            @PathVariable Integer id,
            Authentication authentication) {

        Teacher teacher = getAuthenticatedTeacher(authentication);

        return studentRepository.findByIdAndTeacherId(
                        id, teacher.getId())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Student> addStudent(
            @RequestBody Student student,
            Authentication authentication) {

        Teacher teacher = getAuthenticatedTeacher(authentication);

        // Never trust a teacher ID supplied by the frontend.
        student.setId(null);
        student.setTeacherId(teacher.getId());

        Student savedStudent = studentRepository.save(student);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedStudent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable Integer id,
            @RequestBody Student studentDetails,
            Authentication authentication) {

        Teacher teacher = getAuthenticatedTeacher(authentication);

        return studentRepository.findByIdAndTeacherId(
                        id, teacher.getId())
                .map(student -> {
                    student.setName(studentDetails.getName());
                    student.setEmail(studentDetails.getEmail());
                    student.setCourse(studentDetails.getCourse());
                    student.setAge(studentDetails.getAge());
                    student.setMarks(studentDetails.getMarks());

                    return ResponseEntity.ok(
                            studentRepository.save(student));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable Integer id,
            Authentication authentication) {

        Teacher teacher = getAuthenticatedTeacher(authentication);

        return studentRepository.findByIdAndTeacherId(
                        id, teacher.getId())
                .map(student -> {
                    studentRepository.delete(student);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<Void> deleteMultipleStudents(
            @RequestBody List<Integer> ids,
            Authentication authentication) {

        Teacher teacher = getAuthenticatedTeacher(authentication);

        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        for (Integer id : ids) {
            if (id == null) {
                return ResponseEntity.badRequest().build();
            }
        }

        List<Student> ownedStudents = studentRepository.findAllById(ids)
                .stream()
                .filter(student ->
                        teacher.getId().equals(student.getTeacherId()))
                .toList();

        studentRepository.deleteAll(ownedStudents);

        return ResponseEntity.noContent().build();
    }
}