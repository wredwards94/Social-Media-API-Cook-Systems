package com.cooksys.socialmedia.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@Data
public class UserResponseDto {
	
	private String username;
	
	private ProfileResponseDto profile;
	
	private Timestamp joined;

}
