package com.stephen.login.service;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

import java.util.Optional;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.stephen.login.dto.MfaDto;
import com.stephen.login.entity.User;
import com.stephen.login.exceptions.QrCodeException;
import com.stephen.login.repository.UserRepository;
import com.stephen.login.util.JwtTokenUtil;
import com.stephen.login.util.StringUtil;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;

@Service
public class TotpService {
	
	private UserService userService;
	private UserRepository userRepository;
	private JwtTokenUtil jwtTokenUtil;	
	private UserRolesDetailsService userRolesDetailsService;

	public TotpService(UserService userService, UserRepository userRepository, JwtTokenUtil jwtTokenUtil,
			UserRolesDetailsService userRolesDetailsService) {
		this.userService = userService;
		this.userRepository = userRepository;
		this.jwtTokenUtil = jwtTokenUtil;
		this.userRolesDetailsService = userRolesDetailsService;
	}

	public String generateUriForImage() {
		
		String secret = generateSecret();
		
		User user = userService.getLoggedInUser();
		
        QrData data = new QrData.Builder()
                .label("My MFA Test")
                .secret(StringUtils.isNoneEmpty(user.getTotp()) ? user.getTotp() : secret)
                .issuer(user.getUsername())
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        QrGenerator generator = new ZxingPngQrGenerator();
        byte[] imageData = new byte[0];

        try {
            imageData = generator.generate(data);
            if(Boolean.FALSE.equals(user.getMfaEnabled())) {
            	user.setMfaEnabled(Boolean.TRUE);
            	user.setTotp(secret);
            	userRepository.save(user);
            }
        } catch (QrGenerationException e) {
           throw new QrCodeException("Unable to generate QrCode");
        }

        String mimeType = generator.getImageMimeType();

        return getDataUriForImage(imageData, mimeType);
        
    }
	
	private String generateSecret() {
		Base32 base32 = new Base32();
		return base32.encodeAsString(StringUtil.getSaltString().getBytes());
	}
	
	public Object verifyCode(MfaDto mfaDto) {
		Optional<User> user = userRepository.findByUsername(mfaDto.getUsername());
		if(user.isPresent()) {
			TimeProvider timeProvider = new SystemTimeProvider();
	        CodeGenerator codeGenerator = new DefaultCodeGenerator();
	        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
	        boolean result = verifier.isValidCode(user.get().getTotp(), mfaDto.getCode());
	        if(result) {
	        	final UserDetails userDetails = userRolesDetailsService.loadUserByUsername(mfaDto.getUsername());
	        	final String token = jwtTokenUtil.generateToken(userDetails);
	        	user.get().setAccessToken(token);
	        	return user.get();
	        }
	        return result;	
		} else {
			throw new UsernameNotFoundException("User not found for the username: " + mfaDto.getUsername());
		}
    }

	public void disableMfa() {
		User user = userService.getLoggedInUser();
    	user.setMfaEnabled(Boolean.FALSE);
    	user.setTotp(null);
    	userRepository.save(user);
    }
	
}
