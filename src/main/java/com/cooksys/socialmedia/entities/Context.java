package com.cooksys.socialmedia.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Context {
	private Tweet target;
	
	private List<Tweet> beforeChain;
	
	private List<Tweet> afterChain;
	
	public Context(Tweet tweet) {
		this.target = tweet; 
		this.beforeChain = new ArrayList<>();
		this.afterChain = new ArrayList<>();

        // Build the before chain
        buildBeforeChain(tweet);

        // Build the after chain
        buildAfterChain(tweet);
	}

    private void buildBeforeChain(Tweet tweet) {
        Tweet currentTweet = tweet.getInReplyTo();
        while (currentTweet != null) {
            beforeChain.add(currentTweet);
            currentTweet = currentTweet.getInReplyTo();
        }
    }

    private void buildAfterChain(Tweet tweet) {
    	Tweet currentTweet = tweet.getRepostOf();
        /*
    	List<Tweet> replies = currentTweet.getReplies();
        for(Tweet reply : replies) {
            afterChain.add(reply);
        }
        */
    }
}
