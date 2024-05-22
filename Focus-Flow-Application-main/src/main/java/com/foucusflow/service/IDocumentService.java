package com.foucusflow.service;

import java.io.IOException;

import org.springframework.data.domain.Page;

import com.foucusflow.dto.DocumentFilterDto;
import com.foucusflow.dto.DocumentUploadDto;
import com.foucusflow.dto.UserDto;
import com.foucusflow.entity.Document;

public interface IDocumentService {

	void uploadDocument(DocumentUploadDto docDto) throws IOException;

	Page<Document> fetchDocuments(Integer page);

	void deleteDocById(long id);

	Document fetchById(long id);

	Page<Document> fetchFilteredDocuments(Integer page, DocumentFilterDto docFilterDto);

	Page<Document> fetchDocumentsByStudent(UserDto user, Integer page);
}