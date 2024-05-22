package com.foucusflow.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.foucusflow.dto.UserEditDto;
import com.foucusflow.dto.UserRegDto;
import com.foucusflow.entity.Contacts;
import com.foucusflow.entity.Message;
import com.foucusflow.entity.User;
import com.foucusflow.repo.ContactsRepository;
import com.foucusflow.repo.MessageRepository;
import com.foucusflow.repo.UserRepository;
import com.foucusflow.service.IUserService;
import com.foucusflow.util.ERole;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ContactsRepository contactsRepo;

	@Autowired
	private MessageRepository msgRepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	@Override
	public List<User> getAllChatUser(String requester) {
		List<User> users = new ArrayList<>();

		List<User> dbUsers = new ArrayList<>();

		if (requester.equals("faculty")) {
			dbUsers = userRepo.findAllByRole(ERole.ROLE_STUDENT.name());
		} else if (requester.equals("student")) {
			dbUsers = userRepo.findAllByRole(ERole.ROLE_FACULTY.name());
		}

		dbUsers.forEach(e -> {
			e.getContacts().clear();
			users.add(e);
		});
		return users;
	}

	@Override
	public User getUserByUserName(String userName) {
		return userRepo.findByUsername(userName);
	}

	@Override
	public User getSingleUser(long userId) {
		return userRepo.findById(userId).orElseGet(User::new);
	}

	@Override
	public void registerUser(@Valid UserRegDto userRegDto) throws Exception {
		User user = getUserByUserName(userRegDto.getUserName());

		if (user != null) {
			throw new Exception("Username taken, Please use a different one.");
		}

		user = userRepo.findByEmail(userRegDto.getEmailId());

		if (user != null) {
			throw new Exception("User email taken, Please use a different one.");
		}

		if (!userRegDto.getPassword().equals(userRegDto.getConfirmPwd())) {
			throw new Exception("Password and Cofirm Password are not matching.");
		}

		user = new User();
		user.setFullName(userRegDto.getFirstName() + " " + userRegDto.getLastName());
		user.setUsername(userRegDto.getUserName());
		user.setDummyName(
				StringUtils.isEmpty(userRegDto.getGivenName()) ? generateRandomString(10) : userRegDto.getGivenName());
		user.setEmail(userRegDto.getEmailId());
		user.setPhoneNo(userRegDto.getPhoneNo());
		user.setPassword(passwordEncoder.encode(userRegDto.getPassword()));
		user.setGender(userRegDto.getGender().equalsIgnoreCase("Male") ? "M" : "F");
		user.setBranch(userRegDto.getBranch());
		user.setSemester(userRegDto.getSemester());
		user.setRole(getRole(userRegDto.getRole()));

		userRepo.save(user);
	}

	private String generateRandomString(int length) {
		Random random = new Random();
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int randomIndex = random.nextInt(CHARACTERS.length());
			sb.append(CHARACTERS.charAt(randomIndex));
		}
		return sb.toString();
	}

	private String getRole(String role) {
		String eRole = ERole.ROLE_FACULTY.name();

		if (role.equalsIgnoreCase("Student")) {
			eRole = ERole.ROLE_STUDENT.name();
		}

		return eRole;
	}

	@Override
	public Page<User> listUser(Integer page) {
		Pageable pageable = PageRequest.of(page, 10);
		return userRepo.findAll(pageable);
	}

	@Override
	public void deleteUser(long id) {
		User user = userRepo.findById(id).orElse(null);

		if (user != null) {
			List<Contacts> contacts = user.getContacts();

			for (Contacts contact : contacts) {
				contact.setUser(null);
				contactsRepo.delete(contact);
			}

			List<Message> messages = msgRepo.findAllByToUser(user);
			for (Message message : messages) {
				message.setToUser(null);
				msgRepo.updateUserMessages(id);
				msgRepo.deleteUserMessages(id);
				msgRepo.delete(message);
			}

			userRepo.deleteById(id);
		}
	}

	@Override
	public List<Map<String, String>> getStudentsForEvaluation(Map<String, String> params) {
		List<User> users = userRepo.findByBranchAndSemesterAndRole(params.get("branch"), params.get("semester"),
				ERole.ROLE_STUDENT.name());

		List<Map<String, String>> list = new ArrayList<>();

		for (User user : users) {
			Map<String, String> map = new HashMap<>();
			map.put("id", String.valueOf(user.getId()));
			map.put("name", String.valueOf(user.getFullName()));

			list.add(map);
		}

		return list;
	}

	@Override
	public boolean updatePassword(String userName, String oldPassword, String newPassword) {
		User user = userRepo.findByUsername(userName);

		boolean passwordUpdated = true;

		if (passwordEncoder.matches(oldPassword, user.getPassword())) {
			user.setPassword(passwordEncoder.encode(newPassword));

			userRepo.save(user);
		} else {
			passwordUpdated = false;
		}

		return passwordUpdated;
	}

	@Override
	public void editUserInfo(UserEditDto editDto) throws Exception {
		User findByUsername = userRepo.findByUsername(editDto.getUsername());

		if (findByUsername != null && findByUsername.getId() != Long.parseLong(editDto.getId())) {
			throw new Exception("Username already taken");
		}

		User user = userRepo.findById(Long.parseLong(editDto.getId())).orElse(null);
		if (user != null) {
			user.setFullName(editDto.getFullName());
			user.setUsername(editDto.getUsername());
			user.setDummyName(editDto.getDummyName());
			user.setPhoneNo(editDto.getPhoneNo());
			user.setEmail(editDto.getEmail());
			
			if(!editDto.getProfileImg().isEmpty()) {
				user.setProfileImg(editDto.getProfileImg().getBytes());
			}

			userRepo.save(user);
		} else {
			throw new Exception("User not exist");
		}
	}
}
