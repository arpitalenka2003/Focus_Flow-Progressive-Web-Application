package com.foucusflow.dto;

import org.springframework.web.multipart.MultipartFile;

public class DocumentUploadDto extends DocumentFilterDto{
	
	private String description;
	private MultipartFile[] documents;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MultipartFile[] getDocuments() {
		return documents;
	}

	public void setDocuments(MultipartFile[] documents) {
		this.documents = documents;
	}
}
