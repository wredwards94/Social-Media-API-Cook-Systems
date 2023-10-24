package com.cooksys.socialmedia.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cooksys.socialmedia.dtos.CredentialsRequestDto;
import com.cooksys.socialmedia.dtos.CredentialsResponseDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.dtos.UserRequestDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;
import com.cooksys.socialmedia.entities.Credentials;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.CredentialsMapper;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.mappers.UserMapper;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final CredentialsMapper credentialsMapper;
    private final   UserMapper userMapper;
    private  final UserRepository userRepository;
    private final TweetMapper tweetMapper;
    private final TweetRepository tweetRepository;


    @Override
    public CredentialsResponseDto unfollowUser(String username, CredentialsRequestDto credentialsRequestDto) {
        Credentials credentials = credentialsMapper.requestDtoToEntity(credentialsRequestDto);
        if(credentialsRequestDto.getPassword() == null){
            throw new BadRequestException("Please Provide a password");
        }

        Optional<User> optionalUserToFollow = userRepository.findByCredentialsUsername(username);
        Optional<User> optionalUserToSubscribe = userRepository.findByCredentialsUsername(credentials.getUsername());

        if (!optionalUserToFollow.isPresent() || !optionalUserToSubscribe.isPresent()) {
            throw new NotFoundException("Could not find one or both users");
        }

        User userToFollow = optionalUserToFollow.get();
        User userToSubscribe = optionalUserToSubscribe.get();

        if (userToFollow.getFollowers().contains(userToSubscribe)) {
            userToFollow.getFollowers().remove(userToSubscribe);
            userRepository.saveAndFlush(userToFollow);
        } else if (userToFollow.isDeleted()) {
            throw new NotFoundException("The followable user was softDeleted");

        } else {
            throw new NotFoundException("The followable user was  not followed by the subscriber ");

        }

        return null;
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userMapper.entitiesToDtos(userRepository.findAllByDeletedFalse());
    }


  @Override
  public CredentialsResponseDto followUser(String username, CredentialsRequestDto credentialsRequestDto) {
      Credentials credentials = credentialsMapper.requestDtoToEntity(credentialsRequestDto);
      if(credentialsRequestDto.getPassword() == null){
          throw new BadRequestException("The body is missing password");
      }
      Optional<User> optionalUserToFollow = userRepository.findByCredentialsUsername(username);
      Optional<User> optionalUserToSubscribe = userRepository.findByCredentialsUsername(credentials.getUsername());

      if (!optionalUserToFollow.isPresent() || !optionalUserToSubscribe.isPresent()) {
          throw new NotFoundException("Could not find one or both users");
      }

      User userToFollow = optionalUserToFollow.get();
      User userToSubscribe = optionalUserToSubscribe.get();

      if (userToFollow.getFollowers().contains(userToSubscribe) ) {
          throw new NotFoundException("Could not follow the user because The user is  already a follower");
      } else if (userToFollow.isDeleted()) {
          throw new NotFoundException("Could not follow the user because The user is  softDeleted");

      } else {
          userToFollow.getFollowers().add(userToSubscribe);
          userRepository.saveAndFlush(userToFollow);
      }

      return null;
  }

    @Override
    public List<TweetResponseDto> getUserMentions(String username) {

        Optional<User> optionalUser = userRepository.findByCredentialsUsername(username);

        if (optionalUser.isEmpty()) {
            throw new NotFoundException("No such user exists");
        }

        User user = optionalUser.get();
        List<Tweet> userMentionedTweets = user.getMentionedInTweets();
        List<TweetResponseDto> listOfUserMentionedTweets = new ArrayList<>();

        for (Tweet tweet : userMentionedTweets) {
            if (tweet.getContent() != null && tweet.getContent().contains("@" + username)) {
                listOfUserMentionedTweets.add(tweetMapper.entityToDto(tweet));
            }
        }

        Comparator<TweetResponseDto> reverseChronologicalOrder = (tweet1,tweet2) -> {

            return tweet2.getPostedTime().compareTo(tweet1.getPostedTime());
        };

        Collections.sort(listOfUserMentionedTweets,reverseChronologicalOrder);


        return listOfUserMentionedTweets;
    }

    @Override
    public List<UserResponseDto> getUserFollowers(String username) {
        Optional<User> user = userRepository.findByCredentialsUsername(username);

        if(user.isEmpty()){
            throw new NotFoundException("No such user exist");
        }

        List<User> userFollowers = user.get().getFollowers();
        List<User> nonDeletedUsers = new ArrayList<>();

        for (User individualUser : userFollowers){
            if (!individualUser.isDeleted()){
                nonDeletedUsers.add(individualUser);
            }
        }

        return userMapper.entitiesToDtos(nonDeletedUsers);
    }

    /*
    GET users/@{username}/following
     */
    @Override
    public List<UserResponseDto> getUserFollowing(String username) {
        Optional<User> optionalUser = userRepository.findByCredentialsUsername(username);
        if(optionalUser.isEmpty()) throw new NotFoundException("user does not exists");

        User foundUser = optionalUser.get();
        if(foundUser.isDeleted()) throw new NotFoundException("user does not exists");

//        List<User> userFollowing = foundUser.getFollowing();
        foundUser.getFollowing().removeIf(User::isDeleted);
        return userMapper.entitiesToDtos(foundUser.getFollowing());
    }

    /*
    DELETE users/@{username}
    */
    @Override
    public UserResponseDto softDeleteUser(String username) {
        Optional<User> optionalUser = userRepository.findByCredentialsUsername(username);

        if(optionalUser.isEmpty()) throw new NotFoundException("user does not exists");

        User foundUser = optionalUser.get();
        if(foundUser.isDeleted()) throw new NotFoundException("user does not exists");

        foundUser.setDeleted(true);
        for(Tweet tweet : foundUser.getTweets()) {
            tweet.setDeleted(true);
            tweetRepository.saveAndFlush(tweet);
        }
        return userMapper.entityToDto(userRepository.saveAndFlush(foundUser));
    }

    @Override
    public UserResponseDto updateProfile(String username, UserRequestDto userRequestDto) {
        //check if user exists
        Optional<User> optionalUser = userRepository.findByCredentialsUsername(username);
        if(optionalUser.isEmpty()) {
            throw new NotFoundException("user with that username does not exist");
        }
        User user = optionalUser.get();
        
        //check if credentials are provided in dto
        if(userRequestDto.getCredentials() == null) {
        	throw new BadRequestException("provided credentials are null");
        }
        
        //check if profile was provided in Dto
        if(userRequestDto.getProfile() == null) {
        	throw new BadRequestException("provided profile is null");
        }
        
        //check if credentials match
        if(!(user.getCredentials().getPassword().equals(userRequestDto.getCredentials().getPassword())) ||
           !(user.getCredentials().getUsername().equals(userRequestDto.getCredentials().getUsername()))) {
        	throw new BadRequestException("provided credentials do not match requested username");
        }

        //check if user has been deleted
        if(user.isDeleted()) {
            throw new BadRequestException("user with that username has been deleted");
        }
        
        //update profile with provided fields (if fields are provided in DTO)
        String firstName = userRequestDto.getProfile().getFirstName();
        String lastName = userRequestDto.getProfile().getLastName();
        String email = userRequestDto.getProfile().getEmail();
        String phone = userRequestDto.getProfile().getPhone();
        
        if(firstName != null) {
        	user.getProfile().setFirstName(firstName);
        }
        if(lastName != null) {
        	user.getProfile().setLastName(lastName);
        }
        if(email != null) {
        	user.getProfile().setEmail(email);
        }
        if(phone != null) {
        	user.getProfile().setPhone(phone);
        }
        
        userRepository.saveAndFlush(user);

        //convert to dto and return
        return userMapper.entityToDto(user);
    }

    @Override
    public UserResponseDto addUser(UserRequestDto userRequestDto) {
    	
        //check if credentials are provided in dto
        if(userRequestDto.getCredentials() == null) {
        	throw new BadRequestException("provided credentials are null");
        }
        
        //check if profile was provided in Dto
        if(userRequestDto.getProfile() == null) {
        	throw new BadRequestException("provided profile is null");
        }

        //check if username is taken
        String username = userRequestDto.getCredentials().getUsername();
        Optional<User> optionalUser = userRepository.findByCredentialsUsername(username);
        if(!optionalUser.isEmpty()) {
        	//user exists, check if deleted
        	User user = optionalUser.get();
        	if(user.isDeleted()) {
        		//check credentials and reinstate deleted user
        		if(user.getCredentials().getUsername().equals(userRequestDto.getCredentials().getUsername()) &&
        		   user.getCredentials().getPassword().equals(userRequestDto.getCredentials().getPassword())){
            		user.setDeleted(false);
            		return userMapper.entityToDto(userRepository.saveAndFlush(user));
        		} else {
        			throw new BadRequestException("provided credentials do not match");
        		}
        	} else {
        		//user exists and is not deleted
        		throw new BadRequestException("username is already taken");
        	}
        }
        
        //create new user from dto
        User newUser = userMapper.requestDtoToEntity(userRequestDto);

        //check that all fields are provided
        String password = newUser.getCredentials().getPassword();
        String email = newUser.getProfile().getEmail();

        if(username == null || username.equals("") ||
           password == null || password.equals("") ||
           email == null || email.equals("")) {
            throw new BadRequestException("required field(s) of new user information are missing");
        }

        //post user to database and return user dto
        return userMapper.entityToDto(userRepository.saveAndFlush(newUser));
    }

    @Override
    public List<TweetResponseDto> getUserFeed(String username){
        //check if user exists
        Optional<User> optionalUser = userRepository.findByCredentialsUsername(username);
        if(optionalUser.isEmpty()) {
            throw new BadRequestException("user with that username does not exist");
        }
        User user = optionalUser.get();

        //get all non deleted tweets from user
        List<Tweet> feedTweets = new ArrayList<>();
        for(Tweet tweet : user.getTweets()) {
            if(!tweet.isDeleted()) {
                feedTweets.add(tweet);
            }
        }

        //get all non deleted tweets from users followed by the user provided
        //System.out.println("following: " + user.getFollowing());
        //System.out.println("followers: " + user.getFollowers());
        for(User following : user.getFollowing()) {
            for(Tweet tweet : following.getTweets()) {
                if(!tweet.isDeleted()) {
                    //loop through tweets from each user they follow, and add tweet to list if not deleted
                    feedTweets.add(tweet);
                }
            }
        }

        //sort tweets in reverse chronological order by timestamp
        feedTweets.sort(Comparator.comparing(Tweet::getPostedTime));
        Collections.reverse(feedTweets);

        //convert array of tweets to dtos and return
        return tweetMapper.entitiesToDtos(feedTweets);
    }

    // GET users/@{username}
    @Override
    public UserResponseDto getUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByCredentialsUsername(username);
        if (userOptional.isPresent() && !userOptional.get().isDeleted()) {
            User user = userOptional.get();
            return userMapper.entityToDto(user);
        } else {
            throw new NotFoundException("User not found");
        }
    }

    @Override
    public List<TweetResponseDto> getTweetsByUsername(String username) {
        Optional<User> userOptional = userRepository.findByCredentialsUsername(username);
        if (userOptional.isPresent() && !userOptional.get().isDeleted()) {
            User user = userOptional.get();
            List<Tweet> tweets = tweetRepository.findByAuthorAndDeletedIsFalseOrderByPostedTimeDesc(user);

            return tweetMapper.entitiesToDtos(tweets);
        } else throw new NotFoundException("User not found");
    }
}
