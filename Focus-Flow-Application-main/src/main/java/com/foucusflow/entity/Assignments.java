package com.foucusflow.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "assignments")
public class Assignments {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String branch;

	private String semester;

	private String course;

	private String duration;

	private String title;

	@Lob
	@Column(columnDefinition = "TEXT")
	private String question;

	private String mark;

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
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

	@Override
	public String toString() {
		return "Assignments [id=" + id + ", branch=" + branch + ", semester=" + semester + ", course=" + course
				+ ", duration=" + duration + ", title=" + title + ", question=" + question + ", mark=" + mark
				+ ", mcq1=" + mcq1 + ", mcq2=" + mcq2 + ", mcq3=" + mcq3 + ", mcq4=" + mcq4 + "]";
	}
}