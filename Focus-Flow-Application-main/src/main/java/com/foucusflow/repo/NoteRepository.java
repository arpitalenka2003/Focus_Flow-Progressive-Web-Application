package com.foucusflow.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.foucusflow.entity.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {

	Page<Note> findAllByUserId(long userId, Pageable pageable);
}