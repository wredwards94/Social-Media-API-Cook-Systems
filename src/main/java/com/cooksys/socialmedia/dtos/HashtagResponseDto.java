package com.cooksys.socialmedia.dtos;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.sql.Timestamp;

@RequiredArgsConstructor
@Data
public class HashtagResponseDto {
	private String label;
	
	private Timestamp firstUsed;
	
	private Timestamp lastUsed;
}
