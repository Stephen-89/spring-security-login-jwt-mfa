package com.stephen.login.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stephen.login.dto.AuthModel;
import com.stephen.login.dto.MfaDto;
import com.stephen.login.entity.User;
import com.stephen.login.util.JwtTokenUtil;
import com.stephen.login.controller.AuthController;
import com.stephen.login.service.AuthService;
import com.stephen.login.service.TotpService;
import com.stephen.login.service.UserRolesDetailsService;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @MockBean
    private AuthService authService;
    @MockBean
    private TotpService totpService;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private UserRolesDetailsService userRolesDetailsService;
    
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testLogin() throws Exception {
    	
        AuthModel authModel = mockAuthModel();
        User user = mockUser();

        Mockito.when(authService.loginUser(any(AuthModel.class)))
                .thenReturn(ResponseEntity.ok(user));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authModel)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(user.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user.getUsername()));
        
    }

    @Test
    void testGenerateUriForImage() throws Exception {

    	String uriForImage = "http://example.com/qr-code.png";

        Mockito.when(totpService.generateUriForImage())
                .thenReturn(uriForImage);

        mockMvc.perform(get("/generate-qr-image")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.image").value(uriForImage));
        
    }

    @Test
    void testDisableMfa() throws Exception {
        mockMvc.perform(post("/disable-mfa"))
                .andExpect(status().isOk());
        verify(totpService, times(1)).disableMfa();
    }

    @Test
    void testVerifyCode() throws Exception {

    	MfaDto mfaDto = new MfaDto();
        mfaDto.setCode("123456");

        Object verificationResult = "Verification successful";
        Mockito.when(totpService.verifyCode(any(MfaDto.class)))
                .thenReturn(verificationResult);

        mockMvc.perform(post("/verfiy-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mfaDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(verificationResult));
        
    }
    
	private User mockUser() {
		User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
		return user;
	}

	private AuthModel mockAuthModel() {
        AuthModel authModel = new AuthModel();
        authModel.setUsername("testuser");
        authModel.setPassword("testpass");
		return authModel;
	}
    
}
