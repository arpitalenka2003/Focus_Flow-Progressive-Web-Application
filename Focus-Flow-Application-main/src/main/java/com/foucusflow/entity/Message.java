package com.foucusflow.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class Message {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne
	private User toUser;

	@Lob
	private byte[] content;
	
	private byte[] recievedContent;
	
	private String msgLabel;
	
	private String sendDate;
	
	private String recievedDate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getToUser() {
		return toUser;
	}

	public void setToUser(User toUser) {
		this.toUser = toUser;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getSendDate() {
		return sendDate;
	}

	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}

	public String getRecievedDate() {
		return recievedDate;
	}

	public void setRecievedDate(String recievedDate) {
		this.recievedDate = recievedDate;
	}

	public String getMsgLabel() {
		return msgLabel;
	}

	public void setMsgLabel(String msgLabel) {
		this.msgLabel = msgLabel;
	}

	public byte[] getRecievedContent() {
		return recievedContent;
	}

	public void setRecievedContent(byte[] recievedContent) {
		this.recievedContent = recievedContent;
	}

	public Message(User toUser, byte[] content, byte[] recievedContent, String msgLabel, String sendDate,
			String recievedDate) {
		this.toUser = toUser;
		this.content = content;
		this.recievedContent = recievedContent;
		this.msgLabel = msgLabel;
		this.sendDate = sendDate;
		this.recievedDate = recievedDate;
	}

	public Message(long id, User toUser, byte[] content, byte[] recievedContent, String msgLabel,
			String sendDate, String recievedDate) {
		this.id = id;
		this.toUser = toUser;
		this.content = content;
		this.recievedContent = recievedContent;
		this.msgLabel = msgLabel;
		this.sendDate = sendDate;
		this.recievedDate = recievedDate;
	}

	public Message(User toUser, byte[] content, String msgLabel, String sendDate) {
		super();
		this.toUser = toUser;
		this.content = content;
		this.msgLabel = msgLabel;
		this.sendDate = sendDate;
	}

	public Message() {
	}

	@Override
	public String toString() {
		return "Message [message_id=" + id + ", toUser=" + toUser + ", content=" + content + ", sendDate="
				+ sendDate + ", recievedDate=" + recievedDate + "]";
	}
}