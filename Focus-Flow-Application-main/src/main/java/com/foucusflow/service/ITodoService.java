package com.foucusflow.service;

import org.springframework.data.domain.Page;

import com.foucusflow.dto.CreateTodoDto;
import com.foucusflow.entity.Todo;

public interface ITodoService {

	Page<Todo> fetchTodosByUser(Integer page, long userId);

	void createTodo(long id, CreateTodoDto todoDto);

	void markTodoComplete(long id);

	void markTodoInComplete(long id);
	
	void deleteTodo(long id);

	void editTodo(CreateTodoDto dto);
}
