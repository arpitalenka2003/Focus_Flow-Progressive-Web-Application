package com.foucusflow.dto;

public class AssignmentDto {
	private String question;
	private String answer;
	private String mark;
	private String isMcq;
	private String mcq1;
	private String mcq2;
	private String mcq3;
	private String mcq4;
	
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
	public String getIsMcq() {
		return isMcq;
	}
	public void setIsMcq(String isMcq) {
		this.isMcq = isMcq;
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
		return "AssignmentDto [question=" + question + ", answer=" + answer + ", mark=" + mark + ", isMcq=" + isMcq
				+ ", mcq1=" + mcq1 + ", mcq2=" + mcq2 + ", mcq3=" + mcq3 + ", mcq4=" + mcq4 + "]";
	}
	
}
