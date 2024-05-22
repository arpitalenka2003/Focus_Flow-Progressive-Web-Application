package com.foucusflow.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foucusflow.entity.Todo;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

	Page<Todo> findAllByUserId(long userId, Pageable pageable);

}