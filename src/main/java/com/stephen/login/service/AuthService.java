package com.stephen.login.service;

import org.springframework.http.ResponseEntity;

import com.stephen.login.dto.AuthModel;
import com.stephen.login.entity.User;

public interface AuthService {

	ResponseEntity<User> loginUser(AuthModel authModel) throws Exception;

}
