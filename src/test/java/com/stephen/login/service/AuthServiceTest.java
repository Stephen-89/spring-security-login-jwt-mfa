package com.stephen.login.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;

import com.stephen.login.dto.AuthModel;
import com.stephen.login.entity.User;
import com.stephen.login.exceptions.ResourceNotFoundException;
import com.stephen.login.exceptions.TotpRequiredException;
import com.stephen.login.exceptions.user.UserExpiredException;
import com.stephen.login.exceptions.user.UserLockedException;
import com.stephen.login.repository.UserRepository;
import com.stephen.login.util.JwtTokenUtil;
import com.stephen.login.service.AuthService;
import com.stephen.login.service.UserRolesDetailsService;

@SpringBootTest
class AuthServiceTest {

    @MockBean
    private UserRolesDetailsService userRolesDetailsService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        when(userRolesDetailsService.loadUserByUsername(anyString())).thenReturn(mock(UserDetails.class));
        when(jwtTokenUtil.generateToken(any(UserDetails.class))).thenReturn("dummyToken");
    }

	@Test
    @SuppressWarnings("deprecation")
	void testLoginUserSuccess() throws Exception {
    	
    	AuthModel authModel = mockAuthModel();
        User user = mockUser();
        user.setMfaEnabled(false);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("username");

        when(userRolesDetailsService.checkUserNonExpired(anyString())).thenReturn(false);
        when(userRolesDetailsService.checkUserNonLocked(anyString())).thenReturn(false);
        when(userRepository.findByUsername(anyObject())).thenReturn(Optional.of(user));

        ResponseEntity<User> response = authService.loginUser(authModel);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("dummyToken", response.getBody().getAccessToken());
        
    }

    @Test
    void testLoginUserUserExpired() {
    	
    	AuthModel authModel = mockAuthModel();

        when(userRolesDetailsService.checkUserNonExpired(anyString())).thenReturn(true);

        Exception exception = assertThrows(UserExpiredException.class, () -> {
            authService.loginUser(authModel);
        });

        assertEquals("User Expired", exception.getMessage());
        
    }

    @Test
    void testLoginUserUserLocked() {
    	
    	AuthModel authModel = mockAuthModel();

        when(userRolesDetailsService.checkUserNonExpired(anyString())).thenReturn(false);
        when(userRolesDetailsService.checkUserNonLocked(anyString())).thenReturn(true);

        Exception exception = assertThrows(UserLockedException.class, () -> {
            authService.loginUser(authModel);
        });

        assertEquals("User Locked", exception.getMessage());
        
    }

	@Test
    @SuppressWarnings("deprecation")
	void testLoginUserUserNotFound() {
    	
    	AuthModel authModel = mockAuthModel();

        when(userRolesDetailsService.checkUserNonExpired(anyString())).thenReturn(false);
        when(userRolesDetailsService.checkUserNonLocked(anyString())).thenReturn(false);
        when(userRepository.findByUsername(anyObject())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            authService.loginUser(authModel);
        });

        assertEquals("User Not Found", exception.getMessage());
        
    }

	@Test
    @SuppressWarnings("deprecation")
	void testLoginUserMfaRequired() {
    	
    	AuthModel authModel = mockAuthModel();
    	User user = mockUser();
    	user.setMfaEnabled(true);
    	
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("username");

        when(userRolesDetailsService.checkUserNonExpired(anyString())).thenReturn(false);
        when(userRolesDetailsService.checkUserNonLocked(anyString())).thenReturn(false);
        when(userRepository.findByUsername(anyObject())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(TotpRequiredException.class, () -> {
            authService.loginUser(authModel);
        });

        assertEquals("Multi-Factor Authentication Required", exception.getMessage());
        
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
