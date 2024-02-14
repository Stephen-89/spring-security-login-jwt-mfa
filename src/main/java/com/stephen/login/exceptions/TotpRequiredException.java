package com.stephen.login.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class TotpRequiredException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public TotpRequiredException(String message) {
		super(message);
	}
	
}
