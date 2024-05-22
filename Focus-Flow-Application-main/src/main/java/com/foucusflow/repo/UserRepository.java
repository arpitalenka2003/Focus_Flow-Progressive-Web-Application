package com.foucusflow.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.foucusflow.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByUsername(String username);

	User findByEmail(String email);

	List<User> findAllByRole(String role);

	Page<User> findAll(Pageable pageable);

	List<User> findByBranchAndSemesterAndRole(String branch, String semester, String role);
}
