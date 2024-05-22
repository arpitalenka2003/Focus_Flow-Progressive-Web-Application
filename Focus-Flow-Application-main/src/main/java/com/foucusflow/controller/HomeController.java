package com.foucusflow.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.foucusflow.util.ERole;

@Controller
public class HomeController {

	@RequestMapping("/")
	public String login(Model model) {
		model.addAttribute("title", "LogIn - Focus Flow");
		return "login";
	}

	@RequestMapping("/success")
	public void loginPageRedirect(HttpServletRequest request, HttpServletResponse response, Authentication authResult)
			throws IOException {

		String role = authResult.getAuthorities().toString();

		if (role.contains(ERole.ROLE_ADMIN.name())) {
			response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/admin/dashboard"));
		} else if (role.contains(ERole.ROLE_STUDENT.name())) {
			response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/student/dashboard"));
		} else {
			response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/faculty/dashboard"));
		}
	}
}
