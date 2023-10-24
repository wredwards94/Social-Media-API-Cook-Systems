package com.cooksys.socialmedia.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserRequestDto {
	
//	@NotNull(message = "profile cannot be blank")
	private ProfileRequestDto profile;
	
//	@NotNull(message = "credentials cannot be blank")
	private CredentialsRequestDto credentials;
}
