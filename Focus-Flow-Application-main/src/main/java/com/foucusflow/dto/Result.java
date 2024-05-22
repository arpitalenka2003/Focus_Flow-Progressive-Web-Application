package com.foucusflow.dto;

import java.io.Serializable;

public class Result implements Serializable{
	private static final long serialVersionUID = -7725912324542286716L;
	
	private String content;
	private String type;

	public Result(String content, String type) {
		super();
		this.content = content;
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}