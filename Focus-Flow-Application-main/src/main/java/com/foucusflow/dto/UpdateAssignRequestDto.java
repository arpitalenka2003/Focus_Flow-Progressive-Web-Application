package com.foucusflow.dto;

public class UpdateAssignRequestDto {

	private long id;
	private String title;
	private String duration;
	private String mark;
	private String question;
	private String mcq1;
	private String mcq2;
	private String mcq3;
	private String mcq4;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getMcq1() {
		return mcq1;
	}

	public void setMcq1(String mcq1) {
		this.mcq1 = mcq1;
	}

	public String getMcq2() {
		return mcq2;
	}

	public void setMcq2(String mcq2) {
		this.mcq2 = mcq2;
	}

	public String getMcq3() {
		return mcq3;
	}

	public void setMcq3(String mcq3) {
		this.mcq3 = mcq3;
	}

	public String getMcq4() {
		return mcq4;
	}

	public void setMcq4(String mcq4) {
		this.mcq4 = mcq4;
	}
}