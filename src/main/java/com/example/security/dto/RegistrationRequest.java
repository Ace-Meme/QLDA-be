package com.example.security.dto;

import com.example.model.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RegistrationRequest {

	@NotEmpty(message = "{registration_name_not_empty}")
	private String name;

	@Email(message = "{registration_email_is_not_valid}")
	@NotEmpty(message = "{registration_email_not_empty}")
	private String email;

	@NotEmpty(message = "{registration_username_not_empty}")
	private String username;

	@NotEmpty(message = "{registration_password_not_empty}")
	private String password;

	@NotEmpty(message = "{registration_fullname_not_empty}")
    private String fullName; 

	@NotNull(message = "{registration_gender_not_null}")
    private Gender gender; 

	@NotNull(message = "{registration_birthyear_not_null}")
    private Integer birthYear; 
	
	@NotNull(message = "{registration_phone_invalid}")
    private String phoneNumber;
}
