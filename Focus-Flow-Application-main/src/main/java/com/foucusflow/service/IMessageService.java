package com.foucusflow.service;

import java.util.List;

import com.foucusflow.entity.Message;

public interface IMessageService {

	List<Message> getFriendLastMessages(long userId, long conId);

	List<Message> getContactMessages(long userId, long conId, long loggedInUserId);

	Message saveMessage(long userId, Message message);

	Message getSingleMessage(long recievedMessageId);

	Message updateMessage(Message message);
}