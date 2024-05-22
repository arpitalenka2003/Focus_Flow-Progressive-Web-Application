package com.foucusflow.config;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.foucusflow.entity.Message;
import com.foucusflow.entity.User;
import com.google.gson.Gson;

@Component
public class SocketHandler extends AbstractWebSocketHandler {
	
	static List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
	
	private static Map<String, User> users = new HashMap<>();
	
	Message m = null;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		System.out.println("afterConnectionEstablished");
		User user = (User) session.getAttributes().get("log");

		users.put(session.getId(), user);
		sessions.add(session);
		
		connectBroadcast();
		
		System.out.println(users.toString());
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session);
		User user = (User) session.getAttributes().get("log");
		users.remove(session.getId());
		connectBroadcast();
		disconnectBroadcast(user);
		System.out.println(users.toString());

	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		if (message instanceof TextMessage) {
			m = new Gson().fromJson((String) message.getPayload(), Message.class);
			sendMessageToOneUser(m);
		} else if (message instanceof BinaryMessage) {
			ByteBuffer b = (ByteBuffer) message.getPayload();
			sendByteToOneUser(m, b.array());
		}
	}

	private static void sendMessageToOneUser(Message message) throws IOException {
		for (WebSocketSession endpoint : sessions) {
			synchronized (endpoint) {
				if (endpoint.getId().equals(getSessionId(message.getToUser()))) {
					endpoint.sendMessage(new TextMessage(new Gson().toJson(message)));
				}
			}
		}
	}

	private static void sendByteToOneUser(Message message, byte[] b) throws IOException {
		for (WebSocketSession endpoint : sessions) {
			synchronized (endpoint) {
				if (endpoint.getId().equals(getSessionId(message.getToUser()))) {
					endpoint.sendMessage(new BinaryMessage(b));
				}
			}
		}
	}

	private static String getSessionId(User to) {
		Map<String, User> map = users.entrySet().stream().filter(u -> u.getValue().getId() == to.getId())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		String sessionId = null;
		for (var entry : map.entrySet()) {
			sessionId = entry.getKey();
		}
		return sessionId;
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		super.handleTransportError(session, exception);
	}

	private static void connectBroadcast() throws IOException {
		for (String sessionId : users.keySet()) {
			Gson gson = new Gson();
			User user = users.get(sessionId);
			user.setContacts(List.of());
			System.out.println(user);
			String json = gson.toJson(user);
			for (WebSocketSession endpoint : sessions) {
				synchronized (endpoint) {

					endpoint.sendMessage(new TextMessage(json));
				}
			}
		}
	}

	private static void disconnectBroadcast(User user) throws IOException {
		Gson gson = new Gson();
		user.setContacts(List.of());
		String json = gson.toJson(user);
		for (WebSocketSession endpoint : sessions) {
			synchronized (endpoint) {
				endpoint.sendMessage(new TextMessage(json));
			}
		}
	}
}