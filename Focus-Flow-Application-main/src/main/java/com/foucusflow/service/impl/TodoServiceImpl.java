package com.foucusflow.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.foucusflow.dto.CreateTodoDto;
import com.foucusflow.entity.Todo;
import com.foucusflow.repo.TodoRepository;
import com.foucusflow.service.ITodoService;

@Service
public class TodoServiceImpl implements ITodoService {

	@Autowired
	private TodoRepository todoRepo;

	@Override
	public Page<Todo> fetchTodosByUser(Integer page, long userId) {
		return todoRepo.findAllByUserId(userId, PageRequest.of(page, 10));
	}

	@Override
	public void createTodo(long id, CreateTodoDto todoDto) {
		Todo todo = new Todo();
		todo.setTitle(todoDto.getTitle());
		todo.setDescription(todoDto.getDescription());
		todo.setStartDate(new Date());
		todo.setCompleted(false);
		todo.setUserId(id);

		todoRepo.save(todo);
	}

	@Override
	public void markTodoComplete(long id) {
		Todo todo = todoRepo.findById(id).orElse(null);
		
		if(todo != null) {
			todo.setCompleted(true);
			todo.setEndDate(new Date());
			todoRepo.save(todo);
		}		
	}

	@Override
	public void markTodoInComplete(long id) {
		Todo todo = todoRepo.findById(id).orElse(null);
		
		if(todo != null) {
			todo.setCompleted(false);
			todo.setEndDate(null);
			todoRepo.save(todo);
		}
	}

	@Override
	public void deleteTodo(long id) {
		todoRepo.deleteById(id);
	}

	@Override
	public void editTodo(CreateTodoDto dto) {
		Todo todo = todoRepo.findById(dto.getId()).orElse(null);
		
		if(todo != null) {
			todo.setTitle(dto.getTitle());
			todo.setDescription(dto.getDescription());
			todoRepo.save(todo);
		}
	}
}
