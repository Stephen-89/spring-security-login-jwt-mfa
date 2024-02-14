package com.stephen.login.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class UserDto {
	
	@NotBlank(message = "First Name should not be empty")
	private String firstName;

	@NotBlank(message = "Second Name should not be empty")
	private String secondName;

	@NotNull(message = "Username should not be empty")
	@Email(message = "Enter a valid username")
	private String username;
	
	@NotNull(message = "Password should not be empty")
	@Size(min = 5, message = "Password should be atleast 5 characters")
	private String password;
	
	private boolean accountNonExpired;
	
	private boolean accountNonLocked;
	
	private boolean credentialsNonExpired;
	
	private boolean enabled;
	
	private String code;
	
}
