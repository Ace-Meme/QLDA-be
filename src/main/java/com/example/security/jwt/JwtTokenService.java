package com.example.security.jwt;

import com.example.security.mapper.UserMapper;
import com.example.security.service.UserService;
import com.example.model.User;
import com.example.security.dto.AuthenticatedUserDto;
import com.example.security.dto.LoginRequest;
import com.example.security.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenService {

	private final UserService userService;

	private final JwtTokenManager jwtTokenManager;

	private final AuthenticationManager authenticationManager;

	public LoginResponse getLoginResponse(LoginRequest loginRequest) {

		final String username = loginRequest.getUsername();
		final String password = loginRequest.getPassword();

		final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
				username, password);

		authenticationManager.authenticate(usernamePasswordAuthenticationToken);

		final AuthenticatedUserDto authenticatedUserDto = userService.findAuthenticatedUserByUsername(username);

		final User user = UserMapper.INSTANCE.convertToUser(authenticatedUserDto);
		final String token = jwtTokenManager.generateToken(user);

		log.info("{} has successfully logged in!", user.getUsername());

		// Get the full user object to include all necessary information
		User fullUser = userService.findByUsername(username);

		return new LoginResponse(
			token,
			fullUser.getUsername(),
			fullUser.getName(),
			fullUser.getEmail(),
			fullUser.getFullName(),
			fullUser.getGender(),
			fullUser.getBirthYear(),
			fullUser.getPhoneNumber(),
			fullUser.getUserRole()
		);
	}
}
