package com.stephen.login.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stephen.login.dto.UserDto;
import com.stephen.login.service.UserService;
import com.stephen.login.util.EntityToDtoMapper;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

	private UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	public ResponseEntity<UserDto> save(@Valid @RequestBody UserDto user) {
		return new ResponseEntity<>(EntityToDtoMapper.userToUserDto(userService.createUser(user)), HttpStatus.CREATED);
	}

	@GetMapping("/profile")
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity<UserDto> readUser() {
		return new ResponseEntity<>(EntityToDtoMapper.userToUserDto(userService.getLoggedInUser()), HttpStatus.OK);
	}

	@PutMapping("/profile")
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity<UserDto> updateUser(@RequestBody UserDto user) {
		return new ResponseEntity<>(EntityToDtoMapper.userToUserDto(userService.updateUser(user)), HttpStatus.OK);
	}

	@DeleteMapping("/deactivate")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<HttpStatus> deleteUser() {
		userService.deleteUser();
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PostMapping("/update-message-queue")
	public void update(@RequestParam(name = "id") String id) {
		userService.sendMessage(id);
	}

}
