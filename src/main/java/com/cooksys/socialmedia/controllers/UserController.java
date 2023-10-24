package com.cooksys.socialmedia.controllers;

import com.cooksys.socialmedia.dtos.*;
import com.cooksys.socialmedia.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    //ADD user
    @PostMapping
    public UserResponseDto addUser(@RequestBody @Valid UserRequestDto userRequestDto) {
        return userService.addUser(userRequestDto);
    }

	//PATCH users/@{username}
	@PatchMapping("/@{username}")
	public UserResponseDto updateProfile(@PathVariable String username, @RequestBody UserRequestDto userRequestDto) {
		return userService.updateProfile(username, userRequestDto);
	}

	//GET users/@{username}/feed
	@GetMapping("/@{username}/feed")
	public List<TweetResponseDto> getUserFeed(@PathVariable String username){
		return userService.getUserFeed(username);
	}

    /*
    GET users/@{username}/following
     */
    @GetMapping("/@{username}/following")
    public List<UserResponseDto> getUserFollowing(@PathVariable String username) {
        return userService.getUserFollowing(username);
    }

    /*
    DELETE users/@{username}
    */
    @DeleteMapping("/@{username}")
    public UserResponseDto softDeleteUser(@PathVariable String username) {
        return userService.softDeleteUser(username);
    }

    //GET user
    @GetMapping("/@{username}")
    public UserResponseDto getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    //GET user tweets
    @GetMapping("@{username}/tweets")
    public List<TweetResponseDto> getTweetsByUsername(@PathVariable String username) {
        return userService.getTweetsByUsername(username);
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers(){
       return userService.getAllUsers();
   }

    @PostMapping("/@{username}/follow")
    public CredentialsResponseDto followUser(@PathVariable String username, @RequestBody CredentialsRequestDto credentialsRequestDto){
       return  userService.followUser(username,credentialsRequestDto);
   }
   @PostMapping("/@{username}/unfollow")
    public CredentialsResponseDto unfollowUser(@PathVariable String username,@RequestBody CredentialsRequestDto credentialsRequestDto){
        return userService.unfollowUser(username,credentialsRequestDto);
   }

   @GetMapping("/@{username}/mentions")
    public List<TweetResponseDto> getUserMentions(@PathVariable String username){
        return userService.getUserMentions(username);
   }

   @GetMapping("/@{username}/followers")
    public List<UserResponseDto>getUserFollowers(@PathVariable String username){
        return userService.getUserFollowers(username);
   }
}
