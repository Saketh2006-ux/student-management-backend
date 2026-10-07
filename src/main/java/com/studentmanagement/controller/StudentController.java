package com.studentmanagement.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studentmanagement.entity.Student;
import com.studentmanagement.repository.StudentRepository;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "http://localhost:5173")
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // Get all students
    @GetMapping
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Get student by ID
    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Integer id) {
        return studentRepository.findById(id).orElse(null);
    }

    // Add student
    @PostMapping
    public Student addStudent(@RequestBody Student student) {
        return studentRepository.save(student);
    }

    // Update student
    @PutMapping("/{id}")
    public Student updateStudent(
            @PathVariable Integer id,
            @RequestBody Student studentDetails) {

        Student student = studentRepository.findById(id).orElse(null);

        if (student == null) {
            return null;
        }

        student.setName(studentDetails.getName());
        student.setEmail(studentDetails.getEmail());
        student.setCourse(studentDetails.getCourse());
        student.setAge(studentDetails.getAge());
        student.setMarks(studentDetails.getMarks());

        return studentRepository.save(student);
    }

    // Delete student
    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Integer id) {

        if (!studentRepository.existsById(id)) {
            return "Student not found";
        }

        studentRepository.deleteById(id);
        return "Student deleted successfully";
    }
}