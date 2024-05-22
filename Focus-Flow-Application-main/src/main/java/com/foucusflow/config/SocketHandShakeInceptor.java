package com.foucusflow.config;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.foucusflow.entity.User;
@Component
public class SocketHandShakeInceptor implements HandshakeInterceptor{

    @Override
    public void afterHandshake(ServerHttpRequest arg0, ServerHttpResponse arg1, WebSocketHandler arg2,
            @Nullable Exception arg3) {    
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {
    	try {
        ServletServerHttpRequest httpRequest = (ServletServerHttpRequest) request;
        HttpSession session = httpRequest.getServletRequest().getSession();
        if((User)session.getAttribute("user") == null) {
        	return false;
        }
        attributes.put("log", session.getAttribute("user"));
        System.out.println("Before=================");
    	} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getLocalizedMessage());
			return false;
		}
        return true;
    }
    
}
