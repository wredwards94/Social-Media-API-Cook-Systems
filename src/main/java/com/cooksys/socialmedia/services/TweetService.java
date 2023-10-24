package com.cooksys.socialmedia.services;

import com.cooksys.socialmedia.dtos.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TweetService {

	TweetResponseDto getTweetById(Long id);

	List<HashtagResponseDto> getTweetsWithTag(Long id);

	ContextResponseDto getTweetContext(Long id);

    TweetResponseDto createTweet(TweetRequestDto tweetRequestDto);

    List<TweetResponseDto> getAllTweetsInReverseChronologicalOrder();


    List<TweetResponseDto> getAllRepliesToSpecifiedTweet(Long id);

    List<UserResponseDto> getTweetLikes(Long id);

    List<UserResponseDto> getTweetMentions(Long id);

    TweetResponseDto deleteTweetById(Long id, CredentialsRequestDto credentialsRequestDto);

    ResponseEntity<Object> likeTweetById(Long id, CredentialsRequestDto credentialsRequestDto);

    TweetResponseDto createTweetReply(Long id, TweetRequestDto tweetRequestDto);

    TweetResponseDto createTweetRepost(Long id, CredentialsRequestDto credentialsRequestDto);

    List<TweetResponseDto> getDirectTweetReposts(Long id);
}
