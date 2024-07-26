package com.stephen.login.exceptions.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class UserDetailsException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public UserDetailsException(String message) {
		super(message);
	}

}
