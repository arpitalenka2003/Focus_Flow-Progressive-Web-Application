package com.foucusflow.dto;

public class EvaluateAnswerDto {
	private String id;
	private String mark;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	@Override
	public String toString() {
		return "EvaluateAnswerDto [id=" + id + ", mark=" + mark + "]";
	}
}