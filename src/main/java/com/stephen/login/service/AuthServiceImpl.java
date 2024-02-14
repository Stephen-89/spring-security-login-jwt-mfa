package com.stephen.login.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.stephen.login.entity.AuthModel;
import com.stephen.login.entity.User;
import com.stephen.login.exceptions.TotpRequiredException;
import com.stephen.login.exceptions.UserExpiredException;
import com.stephen.login.exceptions.UserLockedException;
import com.stephen.login.repository.UserRepository;
import com.stephen.login.util.JwtTokenUtil;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserRolesDetailsService userRolesDetailsService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	public ResponseEntity<User> loginUser(AuthModel authModel) throws Exception {

		authenticate(authModel.getUsername(), authModel.getPassword());

		if (!userRolesDetailsService.checkUserNonExpired(authModel.getUsername())) {

			if (!userRolesDetailsService.checkUserNonLocked(authModel.getUsername())) {

				final UserDetails userDetails = userRolesDetailsService.loadUserByUsername(authModel.getUsername());

				Optional<User> user = userRepository.findByUsername(userDetails.getUsername());

				if (user.get().getMfaEnabled()) {
					throw new TotpRequiredException("Multi-Factor Authentication Required");
				} else {

					final String token = jwtTokenUtil.generateToken(userDetails);
					user.get().setAccessToken(token);
					return new ResponseEntity<User>(user.get(), HttpStatus.OK);

				}

			} else {
				throw new UserLockedException("User Locked");
			}

		} else {
			throw new UserExpiredException("User Expired");
		}

	}

	private void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("User disabled");
		} catch (BadCredentialsException e) {
			throw new Exception("Bad Credentials");
		}
	}

}

























