package com.example.security.service;

import com.example.service.UserValidationService;
import com.example.model.User;
import com.example.model.UserRole;
import com.example.security.dto.AuthenticatedUserDto;
import com.example.security.dto.RegistrationRequest;
import com.example.security.dto.RegistrationResponse;
import com.example.security.mapper.UserMapper;
import com.example.utils.GeneralMessageAccessor;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;
import com.example.service.EmailService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private static final String REGISTRATION_SUCCESSFUL = "registration_successful";

	private final UserRepository userRepository;

	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	private final UserValidationService userValidationService;

	private final GeneralMessageAccessor generalMessageAccessor;

	private final EmailService emailService;

	@Override
	public User findByUsername(String username) {

		return userRepository.findByUsername(username);
	}

	@Override
	public RegistrationResponse registerStudent(RegistrationRequest registrationRequest) {
		userValidationService.validateUser(registrationRequest);

		final User user = UserMapper.INSTANCE.convertToUser(registrationRequest);
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setUserRole(UserRole.STUDENT);
		user.setEmailVerified(false);
		user.setEmailVerificationToken(UUID.randomUUID().toString());
		user.setEmailVerificationTokenExpiry(LocalDateTime.now().plusHours(24));

		userRepository.save(user);

		emailService.sendVerificationEmail(user.getEmail(), user.getEmailVerificationToken());

		final String username = registrationRequest.getUsername();
		final String registrationSuccessMessage = generalMessageAccessor.getMessage(null, REGISTRATION_SUCCESSFUL,
				username);

		log.info("{} registered successfully as a student!", username);

		return new RegistrationResponse(registrationSuccessMessage);
	}

	@Override
	public RegistrationResponse registerTeacher(RegistrationRequest registrationRequest) {
		userValidationService.validateUser(registrationRequest);

		final User user = UserMapper.INSTANCE.convertToUser(registrationRequest);
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setUserRole(UserRole.TEACHER);
		user.setEmailVerified(false);
		user.setEmailVerificationToken(UUID.randomUUID().toString());
		user.setEmailVerificationTokenExpiry(LocalDateTime.now().plusHours(24));

		userRepository.save(user);

		emailService.sendVerificationEmail(user.getEmail(), user.getEmailVerificationToken());

		final String username = registrationRequest.getUsername();
		final String registrationSuccessMessage = generalMessageAccessor.getMessage(null, REGISTRATION_SUCCESSFUL,
				username);

		log.info("{} registered successfully as a teacher!", username);

		return new RegistrationResponse(registrationSuccessMessage);
	}

	@Override
	public AuthenticatedUserDto findAuthenticatedUserByUsername(String username) {

		final User user = findByUsername(username);

		return UserMapper.INSTANCE.convertToAuthenticatedUserDto(user);
	}
}
