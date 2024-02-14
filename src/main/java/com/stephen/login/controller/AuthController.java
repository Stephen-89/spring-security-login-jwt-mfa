package com.stephen.login.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.stephen.login.dto.ImageDto;
import com.stephen.login.dto.MfaDto;
import com.stephen.login.entity.AuthModel;
import com.stephen.login.entity.User;
import com.stephen.login.service.AuthService;
import com.stephen.login.service.TotpService;

@RestController
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private TotpService totpService;
	
	@PostMapping("/login")
	public ResponseEntity<User> login(@RequestBody AuthModel authModel) throws Exception {
		return authService.loginUser(authModel);
	}

	@GetMapping("/generate-qr-image")
	public ResponseEntity<ImageDto> generateUriForImage() {
		return new ResponseEntity<ImageDto>(new ImageDto(totpService.generateUriForImage()), HttpStatus.CREATED);
	}

	@PostMapping("/disable-mfa")
	public void disableMfa() {
		totpService.disableMfa();
	}

	@PostMapping("/verfiy-code")
	public ResponseEntity<Object> verifyCode(@Valid @RequestBody MfaDto mfaDto) {
		return new ResponseEntity<Object>(totpService.verifyCode(mfaDto), HttpStatus.OK);
	}
	
}


















