package com.foucusflow.service;

import org.springframework.data.domain.Page;

import com.foucusflow.dto.NoteReqDto;
import com.foucusflow.entity.Note;

public interface INoteService {

	void createNote(long id, NoteReqDto noteDto);

	void deleteNote(long id);

	void editNote(NoteReqDto dto);

	Page<Note> fetchNotesByUser(Integer page, long userId);
}