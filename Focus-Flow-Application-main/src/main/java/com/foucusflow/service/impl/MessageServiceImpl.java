package com.foucusflow.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foucusflow.entity.Message;
import com.foucusflow.repo.MessageRepository;
import com.foucusflow.service.IMessageService;

@Service
public class MessageServiceImpl implements IMessageService {

	@Autowired
	private MessageRepository msgRepo;

	@Override
	public List<Message> getFriendLastMessages(long userId, long conId) {
		return msgRepo.getUserSpecificLastMessage(userId, conId);
	}

	@Override
	public List<Message> getContactMessages(long userId, long conId, long loggedInUserId) {
		List<Message> userSpecificMessage = msgRepo.getUserSpecificMessage(userId, conId);
		
		for(Message msg : userSpecificMessage) {
			if(msg.getToUser().getId() == loggedInUserId) {
				msg.setRecievedDate(LocalDateTime.now().toString());
				updateMessage(msg);
			}
		}
		return msgRepo.getUserSpecificMessage(userId, conId);
	}

	@Override
	public Message saveMessage(long userId, Message message) {
		Message m = msgRepo.save(message);
		msgRepo.saveMyMessage(userId, m.getId());
		return m;
	}

	@Override
	public Message getSingleMessage(long messageId) {
		return msgRepo.findById(messageId).orElseGet(Message::new);
	}

	@Override
	public Message updateMessage(Message message) {
		return msgRepo.save(message);
	}
}