package com.stephen.login.dto;

import lombok.Data;

@Data
public class MfaDto {
	
	private String username;
	private String code;
	
}
