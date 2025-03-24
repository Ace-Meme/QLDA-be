package com.example.security.dto;

import com.example.model.Gender;
import com.example.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {

	private String token;
	private String username;
	private String name;
	private String email;
	private String fullName;
	private Gender gender;
	private Integer birthYear;
	private String phoneNumber;
	private UserRole userRole;
}
