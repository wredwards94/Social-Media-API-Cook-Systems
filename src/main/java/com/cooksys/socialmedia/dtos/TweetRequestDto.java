package com.cooksys.socialmedia.dtos;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class TweetRequestDto {

//	@NotNull(message = "content cannot be blank")
	private String content;
//
//	@NotNull(message = "credentials cannot be blank")
	private CredentialsRequestDto credentials;
}
