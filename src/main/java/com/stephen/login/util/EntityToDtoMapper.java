package com.stephen.login.util;

import com.stephen.login.dto.UserDto;
import com.stephen.login.entity.User;

public class EntityToDtoMapper {

	public static UserDto userToUserDto(User user) {
		UserDto userDto = new UserDto();
		userDto.setFirstName(user.getFirstName());
		userDto.setSecondName(user.getSecondName());
		userDto.setUsername(user.getUsername());
		userDto.setPassword(user.getPassword());
		return userDto;
	}
	
}
