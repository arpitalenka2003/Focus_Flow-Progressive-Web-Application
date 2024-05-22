package com.foucusflow.service.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.foucusflow.dto.DocumentFilterDto;
import com.foucusflow.dto.DocumentUploadDto;
import com.foucusflow.dto.UserDto;
import com.foucusflow.entity.Document;
import com.foucusflow.repo.DocumentRepository;
import com.foucusflow.service.IDocumentService;
import com.foucusflow.specification.DocumentSpecification;

@Service
public class DocumentServiceImpl implements IDocumentService {

	@Autowired
	private DocumentRepository docRepo;

	@Override
	public void uploadDocument(DocumentUploadDto docDto) throws IOException {
		for (MultipartFile file : docDto.getDocuments()) {
			Document doc = new Document();
			doc.setBranch(docDto.getBranch());
			doc.setSemester(docDto.getSemester());
			doc.setCourse(docDto.getCourse());
			doc.setDescription(docDto.getDescription());
			doc.setFileName(file.getOriginalFilename());
			doc.setDocument(file.getBytes());

			docRepo.save(doc);
		}
	}

	@Override
	public Page<Document> fetchDocuments(Integer page) {
		return docRepo.findAll(PageRequest.of(page, 10));
	}

	@Override
	public void deleteDocById(long id) {
		docRepo.deleteById(id);
	}

	@Override
	public Document fetchById(long id) {
		return docRepo.findById(id).orElse(null);
	}

	@Override
	public Page<Document> fetchFilteredDocuments(Integer page, DocumentFilterDto docFilterDto) {

		DocumentSpecification specification = new DocumentSpecification(docFilterDto.getBranch(),
				docFilterDto.getSemester(), docFilterDto.getCourse());

		return docRepo.findAll(specification, PageRequest.of(page, 10));
	}

	@Override
	public Page<Document> fetchDocumentsByStudent(UserDto user, Integer page) {

		DocumentSpecification specification = new DocumentSpecification(user.getBranch(),
				user.getSemester(), "");
		
		return docRepo.findAll(specification, PageRequest.of(page, 10));
	}
}
