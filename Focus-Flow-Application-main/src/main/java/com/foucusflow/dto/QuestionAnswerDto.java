package com.foucusflow.dto;

public class QuestionAnswerDto {

	private String question;
	private String answer;
	private String mark;

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

	@Override
	public String toString() {
		return "QuestionAnswerDto [question=" + question + ", answer=" + answer + ", mark=" + mark + "]";
	}
}