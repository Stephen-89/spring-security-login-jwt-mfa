package com.stephen.login.service;

import com.stephen.login.dto.UserDto;
import com.stephen.login.entity.User;

public interface UserService {
	
	User createUser(UserDto user);
	
	User readUser();
	
	User updateUser(UserDto user);
	
	void deleteUser();
	
	User getLoggedInUser();
	
	void sendMessage(String id);
	
}
