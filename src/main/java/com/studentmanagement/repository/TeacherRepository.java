package com.studentmanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.studentmanagement.entity.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByEmail(String email);

    boolean existsByEmail(String email);
}
