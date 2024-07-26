package com.stephen.login.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.stephen.login.dto.UserDto;
import com.stephen.login.entity.User;
import com.stephen.login.exceptions.user.UserExistsException;
import com.stephen.login.repository.UserRepository;
import com.stephen.login.service.UserService;

@SpringBootTest
class UserServiceTest {

	@MockBean
    private PasswordEncoder bcryptEncoder;
	@MockBean
    private UserRepository userRepository;

	@Autowired
    private UserService userService;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    public void setUp() {

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = new User();
        user.setUsername("testuser");
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        
    }

    @Test
    public void testCreateUser_Success() {
    	
        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setPassword("password");

        User user = new User();
        user.setUsername("testuser");

        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(false);
        when(bcryptEncoder.encode(userDto.getPassword())).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
        
    }

    @Test
    public void testCreateUser_UserExists() {
    	
        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");

        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(true);

        UserExistsException thrown = assertThrows(UserExistsException.class, () -> userService.createUser(userDto));
        assertEquals("User is already registered with username: testuser", thrown.getMessage());
        
    }

    @Test
    public void testUpdateUser_Success() {
    	
        UserDto userDto = new UserDto();
        userDto.setFirstName("John");
        userDto.setPassword("newpassword");
        
        User existingUser = new User();
        existingUser.setUsername("testuser");
        
        //when(userService.getLoggedInUser()).thenReturn(existingUser);
        when(bcryptEncoder.encode(userDto.getPassword())).thenReturn("encodednewpassword");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User result = userService.updateUser(userDto);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
        
    }

    @Test
    public void testUpdateUser_UserNotFound() {
    	
        UserDto userDto = new UserDto();

        when(userService.getLoggedInUser()).thenThrow(new UsernameNotFoundException("User not found"));

        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> userService.updateUser(userDto));
        assertEquals("User not found", thrown.getMessage());
        
    }

    @Test
    public void testDeleteUser_Success() {
    	
    	User user = new User();
        user.setUsername("testuser");

        userService.deleteUser();

        verify(userRepository, times(1)).delete(user);
        
    }

    @Test
    public void testDeleteUser_UserNotFound() {
    	
        when(userService.getLoggedInUser()).thenThrow(new UsernameNotFoundException("User not found"));

        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> userService.deleteUser());
        assertEquals("User not found", thrown.getMessage());
        
    }

    @Test
    public void testGetLoggedInUser_Success() {
    	
        User user = new User();
        user.setUsername("testuser");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User result = userService.getLoggedInUser();

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        
    }

    @Test
    public void testGetLoggedInUser_UserNotFound() {
    	
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> userService.getLoggedInUser());
        assertEquals("User not found for the username: testuser", thrown.getMessage());
        
    }

}
