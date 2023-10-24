package com.cooksys.socialmedia.controllers;


import com.cooksys.socialmedia.dtos.*;
import com.cooksys.socialmedia.services.TweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tweets")
public class TweetController {

    @PostMapping("{id}/reply")
    public TweetResponseDto createTweetReply(@PathVariable Long id, @RequestBody TweetRequestDto tweetRequestDto){

     return tweetService.createTweetReply(id,tweetRequestDto);
  }

  @PostMapping("{id}/repost")
    public TweetResponseDto createTweetRepost(@PathVariable Long id, @RequestBody CredentialsRequestDto credentialsRequestDto){
      return tweetService.createTweetRepost(id,credentialsRequestDto);
  }
  @GetMapping("{id}/reposts")
    public List<TweetResponseDto> getDirectTweetReposts(@PathVariable Long id){
      return tweetService.getDirectTweetReposts(id);
  }

	private final TweetService tweetService;

	//GET tweets/{id}
	@GetMapping("/{id}")
	public TweetResponseDto getTweetById(@PathVariable Long id) {
		return tweetService.getTweetById(id);
	}

	//GET tweets/{id}/tags
	@GetMapping("/{id}/tags")
	public List<HashtagResponseDto> getTweetsWithTag(@PathVariable Long id){
		return tweetService.getTweetsWithTag(id);
	}

	//GET tweets/{id}/context
	@GetMapping("/{id}/context")
	public ContextResponseDto getTweetContext(@PathVariable Long id) {
		return tweetService.getTweetContext(id);
	}

    /*
    * POST tweets
    * Creates a new simple tweet, with the author set to the user identified by the credentials in the request body.
    * If the given credentials do not match an active user in the database, an error should be sent in lieu of a response.
    * The response should contain the newly-created tweet.
    * Because this always creates a simple tweet, it must have a content property and may not have inReplyTo or repostOf properties.
    * IMPORTANT: when a tweet with content is created, the server must process the tweet's content for @{username} mentions and #{hashtag} tags.
    * There is no way to create hashtags or create mentions from the API, so this must be handled automatically!
    * Request: {
    *   content: 'string',
    *   credentials: 'Credentials'
    * }
    *
    * Response: Tweet
    * */
    @PostMapping
    public TweetResponseDto createTweet(@RequestBody TweetRequestDto tweetRequestDto) {
        return tweetService.createTweet(tweetRequestDto);
    }

    //GET tweets/{id}/likes
    /*
    * Retrieves the active users who have liked the tweet with the given id.
    * If that tweet is deleted or otherwise doesn't exist, an error should be sent in lieu of a response.
    * Deleted users should be excluded from the response.
    * Response: [ Users }*/
    @GetMapping("/{id}/likes")
    public List<UserResponseDto> getTweetLikes(@PathVariable Long id) {
        return tweetService.getTweetLikes(id);
    }

    /*
    * GET tweets/{id}/mentions
    * Retrieves the users mentioned in the tweet with the given id.
    * If that tweet is deleted or otherwise doesn't exist, an error should be sent in lieu of a response.Deleted users should be excluded from the response.
    * IMPORTANT Remember that tags and mentions must be parsed by the server!
    * Response: User
    * */
    @GetMapping("/{id}/mentions")
    public List<UserResponseDto> getTweetMentions(@PathVariable Long id) {
        return tweetService.getTweetMentions(id);
    }

    /*
    GET tweets
    Retrieves all (non-deleted) tweets. The tweets should appear in
    reverse-chronological order.
    Response: ['Tweet']
     */
    @GetMapping
    public List<TweetResponseDto> getAllTweets() {
        return tweetService.getAllTweetsInReverseChronologicalOrder();
    }

    /*
    GET tweets/{id}/replies
    Retrieves the direct replies to the tweet with the given id. If that tweet is
    deleted or otherwise doesn't exist, an error should be sent in lieu of a response.
    Deleted replies to the tweet should be excluded from the response.
    Response: ['Tweet']
     */
    @GetMapping("/{id}/replies")
    public List<TweetResponseDto> getAllRepliesToSpecifiedTweet(@PathVariable Long id){
        return tweetService.getAllRepliesToSpecifiedTweet(id);
    }

    @DeleteMapping("/{id}")
    public TweetResponseDto deleteTweetById(@PathVariable Long id, @RequestBody(required = false) CredentialsRequestDto credentialsRequestDto){
    return tweetService.deleteTweetById(id, credentialsRequestDto);
    }

    /*
    POST    tweets/{id}/like
    Creates a "like" relationship between the tweet with the given id and the user whose credentials are
    provided by the request body. If the tweet is deleted or otherwise doesn't exist, or if the given
    credentials do not match an active user in the database, an error should be sent. Following successful
    completion of the operation, no response body is sent.
    Request:'Credentials'
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Object> likeTweetById(@PathVariable Long id, @RequestBody CredentialsRequestDto credentialsRequestDto){
        return tweetService.likeTweetById(id, credentialsRequestDto);
    }

}
