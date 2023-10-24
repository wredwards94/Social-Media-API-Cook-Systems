package com.cooksys.socialmedia.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cooksys.socialmedia.dtos.HashtagResponseDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.entities.Hashtag;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.mappers.HashtagMapper;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.repositories.HashtagRepository;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.services.HashtagService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {
    private final HashtagMapper hashtagMapper;
    private final HashtagRepository hashtagRepository;
    private final TweetRepository tweetRepository;
    private final TweetMapper tweetMapper;

    @Override
    public List<HashtagResponseDto> getAllTags() {
        return hashtagMapper.entitiesToDtos(hashtagRepository.findAll());
    }

    public List<TweetResponseDto> getTweetsWithHashtag(String label) {

        //use example class from spring boot to find hashtag by label in database
        Hashtag tempTag = new Hashtag();
        
        Optional<Hashtag> optionalHashtag = hashtagRepository.findByLabel(label);

        //if hashtag was not found, throw error
        if(optionalHashtag.isEmpty()) {
            throw new BadRequestException("hashtag not found");
        }
        tempTag = optionalHashtag.get();

        //get list of all tweets with hashtag from database, convert to response dto, save in arraylist and return
        List<Tweet> tweetsWithTag = new ArrayList<>();

        //get all tweets from database and check their hashtag lists for match
        List<Tweet> tweets = tweetRepository.findAll();

        for(Tweet tweet : tweets) {
            if(tweet.getHashtags().contains(tempTag)) {
                //save tweet to list
                tweetsWithTag.add(tweet);
            }
        }

        //sort tweets in reverse chronological order by timestamp
        tweetsWithTag.sort(Comparator.comparing(Tweet::getPostedTime));
        Collections.reverse(tweetsWithTag);

        //convert list of tweets to list of tweet response dtos and return
        return tweetMapper.entitiesToDtos(tweetsWithTag);
    }
}
