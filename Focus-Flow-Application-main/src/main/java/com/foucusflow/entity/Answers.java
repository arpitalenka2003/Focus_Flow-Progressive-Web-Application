package com.foucusflow.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "answers")
public class Answers {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String question;
	
	private String answer;
	
	private String mark;
	
	private String markSecured;
	
	@ManyToOne
    @JoinColumn(name = "stuassignments_id")
    private StudentAssignments stuAssignments;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getMarkSecured() {
		return markSecured;
	}

	public void setMarkSecured(String markSecured) {
		this.markSecured = markSecured;
	}

	public StudentAssignments getStuAssignments() {
		return stuAssignments;
	}

	public void setStuAssignments(StudentAssignments stuAssignments) {
		this.stuAssignments = stuAssignments;
	}
}