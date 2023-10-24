package com.cooksys.socialmedia.services;

import java.util.List;
import com.cooksys.socialmedia.dtos.UserRequestDto;
import com.cooksys.socialmedia.dtos.CredentialsRequestDto;
import com.cooksys.socialmedia.dtos.CredentialsResponseDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;

import java.util.List;

public interface UserService {

	UserResponseDto updateProfile(String username, UserRequestDto userRequestDto);

	UserResponseDto addUser(UserRequestDto userRequestDto);

	List<TweetResponseDto> getUserFeed(String username);

    UserResponseDto softDeleteUser(String username);

    List<UserResponseDto> getUserFollowing(String username);

    UserResponseDto getUserByUsername(String username);

    List<TweetResponseDto> getTweetsByUsername(String username);

    List<UserResponseDto> getAllUsers();

    CredentialsResponseDto unfollowUser(String username, CredentialsRequestDto credentialsRequestDto);

    CredentialsResponseDto followUser(String username, CredentialsRequestDto credentialsRequestDto);

    List<TweetResponseDto> getUserMentions(String username);

    List<UserResponseDto> getUserFollowers(String username);
}
