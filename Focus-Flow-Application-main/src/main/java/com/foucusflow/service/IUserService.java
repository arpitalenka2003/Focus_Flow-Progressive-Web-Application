package com.foucusflow.service;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.data.domain.Page;

import com.foucusflow.dto.UserEditDto;
import com.foucusflow.dto.UserRegDto;
import com.foucusflow.entity.User;

public interface IUserService {

	User getUserByUserName(String userName);

	List<User> getAllChatUser(String requester);

	User getSingleUser(long userId);

	void registerUser(@Valid UserRegDto userRegDto) throws Exception;

	Page<User> listUser(Integer page);

	void deleteUser(long id);

	List<Map<String, String>> getStudentsForEvaluation(Map<String, String> params);

	boolean updatePassword(String userName, String oldPassword, String newPassword);

	void editUserInfo(UserEditDto editDto) throws Exception;
}