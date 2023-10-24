package com.cooksys.socialmedia.dtos;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ContextResponseDto {

	  private TweetResponseDto target;
	  
	  private List<TweetResponseDto> before;
	  
	  private List<TweetResponseDto> after;
}
