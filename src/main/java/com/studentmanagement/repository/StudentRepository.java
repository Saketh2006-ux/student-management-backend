package com.studentmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.studentmanagement.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    List<Student> findAllByTeacherId(Long teacherId);

    Optional<Student> findByIdAndTeacherId(Integer id, Long teacherId);

    boolean existsByIdAndTeacherId(Integer id, Long teacherId);

    long countByTeacherId(Long teacherId);
}