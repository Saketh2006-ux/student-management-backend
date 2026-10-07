package com.studentmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.studentmanagement.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {
}