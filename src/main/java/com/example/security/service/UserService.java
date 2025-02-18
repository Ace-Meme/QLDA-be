package com.example.security.service;

import com.example.model.User;
import com.example.security.dto.AuthenticatedUserDto;
import com.example.security.dto.RegistrationRequest;
import com.example.security.dto.RegistrationResponse;

public interface UserService {

	User findByUsername(String username);

	RegistrationResponse registration(RegistrationRequest registrationRequest);

	AuthenticatedUserDto findAuthenticatedUserByUsername(String username);

}
