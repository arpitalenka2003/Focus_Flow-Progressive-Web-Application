package com.foucusflow.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.foucusflow.entity.Answers;

public interface AnswersRepository extends JpaRepository<Answers, Long>{

	@Query(value = "select * from answers where stuassignments_id = :id", nativeQuery = true)
	List<Answers> findAllByStuAssignmentId(long id);

}
