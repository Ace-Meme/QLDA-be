package com.example.security.dto;

import com.example.model.Gender;
import com.example.model.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthenticatedUserDto {

	private String name;

	private String username;

	private String password;

	private UserRole userRole;

	private boolean emailVerified;
	
	private String email;
	private String fullName;
	private Gender gender;
	private Integer birthYear;
	private String phoneNumber;
}
