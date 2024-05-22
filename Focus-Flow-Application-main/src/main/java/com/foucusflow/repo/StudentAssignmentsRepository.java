package com.foucusflow.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foucusflow.entity.StudentAssignments;


public interface StudentAssignmentsRepository extends JpaRepository<StudentAssignments, Long> {

	StudentAssignments findByStudentIdAndTitle(long studentId, String title);

	List<StudentAssignments> findAllByStudentId(long parseLong);
}