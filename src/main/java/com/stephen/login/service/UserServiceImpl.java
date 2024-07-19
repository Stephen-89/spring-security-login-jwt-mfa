package com.stephen.login.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.stephen.login.constants.Constants;
import com.stephen.login.dto.UserDto;
import com.stephen.login.entity.Role;
import com.stephen.login.entity.User;
import com.stephen.login.exceptions.UserExistsException;
import com.stephen.login.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
	
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private PasswordEncoder bcryptEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
    private JmsTemplate jmsTemplate;
	
	@Override
	public User createUser(UserDto user) {
		if (userRepository.existsByUsername(user.getUsername())) {
			throw new UserExistsException("User is already registered with username: " + user.getUsername());
		}
		User newUser = new User();
		BeanUtils.copyProperties(user, newUser);
		newUser.setPassword(bcryptEncoder.encode(newUser.getPassword()));
		newUser.setCredentialsNonExpired(Boolean.TRUE);
		newUser.setAccountNonExpired(Boolean.TRUE);
		newUser.setAccountNonLocked(Boolean.TRUE);
		newUser.setEnabled(Boolean.TRUE);
		newUser.setMfaEnabled(Boolean.FALSE);
		newUser.setRoles(Arrays.asList(new Role(2l, "ROLE_USER")));
		return userRepository.save(newUser);
	}

	@Override
	public User readUser() {
		Long userId = getLoggedInUser().getId();
		return userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found for the id: " +userId));
	}

	@Override
	public User updateUser(UserDto user) {
		User existingUser = readUser();
		existingUser.setFirstName(user.getFirstName() != null ? user.getFirstName() : existingUser.getFirstName());
		existingUser.setSecondName(user.getSecondName() != null ? user.getSecondName() : existingUser.getSecondName());
		existingUser.setUsername(user.getUsername() != null ? user.getUsername() : existingUser.getUsername());
		existingUser.setPassword(user.getPassword() != null ? bcryptEncoder.encode(user.getPassword()) : existingUser.getPassword());
		return userRepository.save(existingUser);
	}

	@Override
	public void deleteUser() {
		User existingUser = readUser();
		userRepository.delete(existingUser);
	}

	@Override
	public User getLoggedInUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found for the username: " + username));
	}

    @Override
    public void sendMessage(String id) {
        Map<String, String> actionmap = new HashMap<>();
        actionmap.put("id", id);
        log.info("Sending the index request through queue message");
        jmsTemplate.convertAndSend(Constants.USER_MESSAGE_QUEUE, actionmap);
    }
    
}

























