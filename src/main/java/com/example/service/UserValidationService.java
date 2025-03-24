package com.example.service;

import java.time.LocalDateTime;
import com.example.model.User;
import com.example.utils.ExceptionMessageAccessor;
import com.example.exceptions.RegistrationException;
import com.example.repository.UserRepository;
import com.example.security.dto.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserValidationService {

	private static final String EMAIL_ALREADY_EXISTS = "email_already_exists";

	private static final String USERNAME_ALREADY_EXISTS = "username_already_exists";

	private static final String INVALID_VERIFICATION_TOKEN = "invalid_verification_token";

	private final UserRepository userRepository;

	private final ExceptionMessageAccessor exceptionMessageAccessor;

	public void validateUser(RegistrationRequest registrationRequest) {

		final String email = registrationRequest.getEmail();
		final String username = registrationRequest.getUserName();

		checkEmail(email);
		checkUsername(username);
	}

	private void checkUsername(String username) {

		final boolean existsByUsername = userRepository.existsByUsername(username);

		if (existsByUsername) {

			log.warn("{} is already being used!", username);

			final String existsUsername = exceptionMessageAccessor.getMessage(null, USERNAME_ALREADY_EXISTS);
			throw new RegistrationException(existsUsername);
		}

	}

	private void checkEmail(String email) {

		final boolean existsByEmail = userRepository.existsByEmail(email);

		if (existsByEmail) {

			log.warn("{} is already being used!", email);

			final String existsEmail = exceptionMessageAccessor.getMessage(null, EMAIL_ALREADY_EXISTS);
			throw new RegistrationException(existsEmail);
		}
	}

	public void verifyEmail(String token) {
		User user = userRepository.findByEmailVerificationToken(token)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid verification token"));

		if (user.isEmailVerified()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already verified");
		}

		if (user.getEmailVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification token has expired");
		}

		user.setEmailVerified(true);
		user.setEmailVerificationToken(null);
		user.setEmailVerificationTokenExpiry(null);
		userRepository.save(user);
	}

}
