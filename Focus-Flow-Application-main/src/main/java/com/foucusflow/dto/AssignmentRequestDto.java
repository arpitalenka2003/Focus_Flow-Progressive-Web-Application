package com.foucusflow.dto;

import java.util.List;

public class AssignmentRequestDto {

	private String branch;
	private String semester;
	private String course;
	private String duration;
	private String title;
	private List<AssignmentDto> assignments;

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public List<AssignmentDto> getAssignments() {
		return assignments;
	}

	public void setAssignments(List<AssignmentDto> assignments) {
		this.assignments = assignments;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "AssignmentRequestDto [branch=" + branch + ", semester=" + semester + ", course=" + course
				+ ", duration=" + duration + ", title=" + title + ", assignments=" + assignments + "]";
	}

}