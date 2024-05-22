package com.foucusflow.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.foucusflow.dto.NoteReqDto;
import com.foucusflow.entity.Note;
import com.foucusflow.repo.NoteRepository;
import com.foucusflow.service.INoteService;

@Service
public class NoteServiceImpl implements INoteService {

	@Autowired
	private NoteRepository noteRepo;

	@Override
	public void createNote(long id, NoteReqDto noteDto) {
		Note note = new Note();
		note.setTitle(noteDto.getTitle());
		note.setContent(noteDto.getContent());
		note.setUserId(id);

		noteRepo.save(note);
	}
	
	@Override
	public void deleteNote(long id) {
		noteRepo.deleteById(id);
	}

	@Override
	public void editNote(NoteReqDto dto) {
		Note note = noteRepo.findById(dto.getId()).orElse(null);
		
		if(note != null) {
			note.setTitle(dto.getTitle());
			note.setContent(dto.getContent());
			noteRepo.save(note);
		}
	}
	
	@Override
	public Page<Note> fetchNotesByUser(Integer page, long userId) {
		return noteRepo.findAllByUserId(userId, PageRequest.of(page, 10));
	}
}
