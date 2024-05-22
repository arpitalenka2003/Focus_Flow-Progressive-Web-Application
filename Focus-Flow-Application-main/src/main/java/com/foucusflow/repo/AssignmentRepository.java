package com.foucusflow.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.foucusflow.entity.Assignments;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignments, Long>, JpaSpecificationExecutor<Assignments> {

	List<Assignments> findAllByTitle(String title);

}
