package com.foucusflow.controller;

import java.security.Principal;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.foucusflow.dto.RegisterResponse;
import com.foucusflow.dto.UserRegDto;
import com.foucusflow.entity.User;
import com.foucusflow.service.IUserService;
import com.foucusflow.util.AppConstant;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private IUserService userService;
	
	public static final String REG_PAGE_URL = "admin/register";

	@RequestMapping("/dashboard")
	public String adminDashboard(Model model, HttpSession session, Principal principal) {
		String userName = principal.getName();
		User user = userService.getUserByUserName(userName);
		session.setAttribute("user", user);
		session.setAttribute(AppConstant.USER_NAME, userName);
		model.addAttribute(AppConstant.USER_NAME, userName);
		return "admin/admin_dashboard";
	}

	@RequestMapping("/dashboard/profile/{name}")
	public String profile(@PathVariable("name") String name, Model model, Principal principal) {
		String userName = principal.getName();
		User user = userService.getUserByUserName(userName);
		model.addAttribute(AppConstant.USER_NAME, userName);
		model.addAttribute("user", user);

		return "admin/about";
	}

	@RequestMapping("/dashboard/register")
	public String register(Model model, Principal principal) {
		String userName = principal.getName();
		User user = userService.getUserByUserName(userName);
		model.addAttribute(AppConstant.USER_NAME, userName);
		model.addAttribute("user", user);
		model.addAttribute("userDto", new UserRegDto());

		return REG_PAGE_URL;
	}

	@RequestMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("userDto") UserRegDto userRegDto, BindingResult result, Model model, HttpSession session) {
		try {
			
			if(result.hasErrors()) {
				System.out.println(result.toString());
				model.addAttribute("userDto", userRegDto);
				return REG_PAGE_URL;
			}
			
			userService.registerUser(userRegDto);

			model.addAttribute("userDto", new UserRegDto());
			session.setAttribute("registerResponse", new RegisterResponse("Successfully Registered", "success"));
		} catch (Exception e) {
			session.setAttribute("registerResponse",
					new RegisterResponse(e.getMessage(), "danger"));
		}
		
		return REG_PAGE_URL;
	}
	
	@RequestMapping("/dashboard/user/view/{page}")
	public String listUser(@PathVariable("page") Integer page, Model model, HttpSession session) {
		
		String userName = (String) session.getAttribute(AppConstant.USER_NAME);
		
		if(!StringUtils.isEmpty(userName)) {
			Page<User> users = userService.listUser(page);
			
			model.addAttribute("users", users);
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", users.getTotalPages());
		}
		
		return "admin/showUser";
	}
	
	@RequestMapping("/dashboard/user/delete/{id}")
	public String deleteUser(@PathVariable("id") long id, Model model, HttpSession session) {
		
		String userName = (String) session.getAttribute(AppConstant.USER_NAME);
		
		if(!StringUtils.isEmpty(userName)) {
			userService.deleteUser(id);
			
			session.setAttribute("message", new RegisterResponse("Successfully Deleted", "alert-success"));
		}
		
		return "redirect:/admin/dashboard/user/view/0";
	}
}
