package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.dtos.*;
import com.cooksys.socialmedia.entities.Hashtag;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.exceptions.NotAuthorizedException;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.HashtagMapper;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.mappers.UserMapper;
import com.cooksys.socialmedia.repositories.HashtagRepository;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {
    private final TweetMapper tweetMapper;
    private final TweetRepository tweetRepository;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final HashtagMapper hashtagMapper;
    private final HashtagRepository hashtagRepository;

//  POST tweets
    @Override
    public TweetResponseDto createTweet(TweetRequestDto tweetRequestDto) {
        if(tweetRequestDto.getCredentials() == null || tweetRequestDto.getCredentials().getUsername() == null ||
                tweetRequestDto.getCredentials().getPassword() == null || tweetRequestDto.getContent() == null) {
            throw new BadRequestException("one or more fields missing");
        }

        Optional<User> optionalUser = userRepository.findByCredentialsUsername(tweetRequestDto.getCredentials().getUsername());
        if(optionalUser.isEmpty()) throw new NotAuthorizedException("non-active user");

        User foundUser = optionalUser.get();
        if(foundUser.isDeleted()) throw new NotAuthorizedException("non-active user");
        if(!foundUser.getCredentials().getPassword().equals(tweetRequestDto.getCredentials().getPassword())) {
            throw new NotAuthorizedException("password incorrect");
        }

        Tweet newTweet = tweetMapper.requestDtoToEntity(tweetRequestDto);
        newTweet.setContent(tweetRequestDto.getContent());

        List<User> mentionedUsers = extractMentions(newTweet.getContent());
        List<Hashtag> hashtags = addHashtagToDb(newTweet.getContent());

        newTweet.setAuthor(foundUser);
        newTweet.setMentionedUsers(mentionedUsers);
        newTweet.setHashtags(hashtags);

        return tweetMapper.entityToDto(tweetRepository.saveAndFlush(newTweet));
    }

    //GET tweets
    @Override
    public List<TweetResponseDto> getAllTweetsInReverseChronologicalOrder() {
        List<Tweet> tweets = tweetRepository.findByDeletedIsFalseOrderByPostedTimeDesc();
        return tweetMapper.entitiesToDtos(tweets);
        //Todo add any exception handling here:
    }

    //GET tweets/{id}/replies
    @Override
    public List<TweetResponseDto> getAllRepliesToSpecifiedTweet(Long id) {
        Optional<Tweet> tweetOptional = tweetRepository.findById(id);
        if (tweetOptional.isPresent()) {
            Tweet tweet = tweetOptional.get();
            List<Tweet> replies = tweet.getReplies();
            return tweetMapper.entitiesToDtos(replies);
        } else throw new NotFoundException("Tweet with id:" + id + " not found.");
    }

    //DELETE tweets/{id}
    @Override
    public TweetResponseDto deleteTweetById(Long id, CredentialsRequestDto credentialsRequestDto) {
        if (credentialsRequestDto.getUsername() == null || credentialsRequestDto.getPassword() == null) {
            throw new BadRequestException("Credentials are required to delete a Tweet");
        }
        Optional<Tweet> tweetOptional = tweetRepository.findById(id);
        if (tweetOptional.isPresent()) {
            Tweet tweet = tweetOptional.get();

            if (tweet.isDeleted()) {
                throw new BadRequestException("Tweet is already deleted");
            }

            Optional<User> userOptional = userRepository.findByCredentialsUsernameAndCredentialsPassword(credentialsRequestDto.getUsername(), credentialsRequestDto.getPassword());
            if (userOptional.isPresent()) {
                tweet.setDeleted(true);
                tweetRepository.save(tweet);
                return tweetMapper.entityToDto(tweet);
            } else {
                throw new NotAuthorizedException("Unauthorized action");
            }
        } else {
            throw new NotFoundException("Tweet not found");
        }
    }

    @Override
    public ResponseEntity<Object> likeTweetById(Long id, CredentialsRequestDto credentialsRequestDto) {
        if (credentialsRequestDto.getUsername() == null || credentialsRequestDto.getPassword() == null) {
            throw new BadRequestException("Credentials are required to 'like' a Tweet");
        }
        Optional<Tweet> tweetOptional = tweetRepository.findById(id);
        if (tweetOptional.isEmpty()) {
            throw new NotFoundException("Tweet not found");
        }
        if (tweetOptional.get().isDeleted()) {
            throw new NotFoundException("This tweet has been deleted");
        }
        Tweet tweetToLike = tweetOptional.get();

        Optional<User> userOptional = userRepository.findByCredentialsUsernameAndCredentialsPassword(credentialsRequestDto.getUsername(), credentialsRequestDto.getPassword());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<Tweet> likesList = user.getLikedTweets();
            if(!likesList.contains(tweetToLike)) likesList.add(tweetToLike);
            user.setLikedTweets(likesList);
            userRepository.saveAndFlush(user);
            return ResponseEntity.noContent().build(); // Return 204 No Content
        } else {
            throw new NotFoundException("User not found");
        }
    }

    /*
     * GET tweets/{id}/likes
     */
    @Override
    public List<UserResponseDto> getTweetLikes(Long id) {
        Optional<Tweet> optionalTweet = tweetRepository.findById(id);

        if(optionalTweet.isEmpty()) throw new NotFoundException("tweet does not exist");

        Tweet foundTweet = optionalTweet.get();
        if(foundTweet.isDeleted()) throw new BadRequestException("this tweet is deleted");

        List<User> likedByUsers = foundTweet.getLikedByUsers();

        likedByUsers.removeIf(User::isDeleted);
        return userMapper.entitiesToDtos(likedByUsers);
    }

    /*
     * GET tweets/{id}/mentions
     * */
    @Override
    public List<UserResponseDto> getTweetMentions(Long id) {
        Optional<Tweet> optionalTweet = tweetRepository.findById(id);

        if(optionalTweet.isEmpty()) throw new NotFoundException("this tweet does not exists");

        Tweet tweet = optionalTweet.get();
        if(tweet.isDeleted()) throw new BadRequestException("deleted tweet");

        List<User> mentions = tweet.getMentionedUsers();
        mentions.removeIf(User::isDeleted);
        return userMapper.entitiesToDtos(mentions);
    }

    @Override
    public TweetResponseDto getTweetById(Long id) {
        //get tweet from repository by id
        Optional<Tweet> optionalTweet = tweetRepository.findById(id);

        //check if tweet exists
        if(optionalTweet.isEmpty()) {
            throw new NotFoundException("tweet with that id not found");
        }
        Tweet tweet = optionalTweet.get();

        //check for deleted tweet
        if(tweet.isDeleted()) {
            throw new BadRequestException("this tweet has been deleted");
        }

        //convert tweet to dto and return
        return tweetMapper.entityToDto(tweet);
    }

    @Override
    public List<HashtagResponseDto> getTweetsWithTag(Long id) {
        //find tweet with provided id
        Optional<Tweet> optionalTweet = tweetRepository.findById(id);

        //check if tweet exists
        if(optionalTweet.isEmpty()) {
            throw new NotFoundException("tweet with that id not found");
        }
        Tweet tweet = optionalTweet.get();

        //check for deleted tweet
        if(tweet.isDeleted()) {
            throw new BadRequestException("this tweet has been deleted");
        }

        //get all hashtags from tweet abd convert list of hashtags to list of dtos and return
        return hashtagMapper.entitiesToDtos(tweet.getHashtags());
    }

    @Override
    public ContextResponseDto getTweetContext(Long id) {
        //find tweet with provided id
        Optional<Tweet> optionalTweet = tweetRepository.findById(id);

        //check if tweet exists
        if(optionalTweet.isEmpty()) {
            throw new NotFoundException("tweet with that id not found");
        }
        Tweet tweet = optionalTweet.get();

        //check for deleted tweet
        if(tweet.isDeleted()) {
            throw new BadRequestException("this tweet has been deleted");
        }

        //get context of tweet
        ContextResponseDto context = new ContextResponseDto();

        //set target
        context.setTarget(tweetMapper.entityToDto(tweet));

        // Build the before chain
        List<Tweet> beforeChain = new ArrayList<>();
        Tweet currentTweet = tweet.getInReplyTo();
        while (currentTweet != null) {
            //only add non deleted tweets to before chain
            if(!currentTweet.isDeleted()) {
                beforeChain.add(currentTweet);
            }
            currentTweet = currentTweet.getInReplyTo();
        }
        context.setBefore(tweetMapper.entitiesToDtos(beforeChain));

        // Build the after chain
        List<Tweet> afterChain = new ArrayList<>();
        List<Tweet> replies = tweet.getReplies();
        afterChain.addAll(replies);
        for(Tweet reply : replies) {
            //only add non deleted tweets to after chain
            if(!reply.isDeleted()) {
                afterChain.addAll(reply.getReplies());
            }
        }
        context.setAfter(tweetMapper.entitiesToDtos(afterChain));

        //return context
        return context;
    }


    @Override
    public TweetResponseDto createTweetReply(Long id, TweetRequestDto tweetRequestDto) {

        Optional<Tweet> tweetOptional = tweetRepository.findById(id);

        if (tweetOptional.isEmpty() || tweetOptional.get().isDeleted()) {
            throw new NotFoundException("No such tweet exists or it is soft-deleted");
        }

        Tweet tweetOriginal = tweetOptional.get();

        if (tweetRequestDto.getContent() == null) {
            throw new BadRequestException("The tweet should have a content field");
        }

        // Check if the user with the provided credentials exists in the database
        Optional<User> userOptional = userRepository.findByCredentialsUsername(tweetRequestDto.getCredentials().getUsername());

        if (userOptional.isEmpty()) {
            throw new NotFoundException("No such user exists with the corresponding credentials");
        }

        User author = userOptional.get();

        // Check if the author of the reply tweet matches the user with provided credentials
        if (!author.getCredentials().getPassword().equals(tweetRequestDto.getCredentials().getPassword())) {
            throw new NotAuthorizedException("Not authorized to create a reply");
        }

        // Extracting the content
        String content = tweetRequestDto.getContent();
        // Extracting users from the content
        List<User> userMentioned = extractMentions(content);
        // Extracting HashTags from the content
        List<Hashtag> hashTagsMentioned = extractHashTag(content);
        System.out.println(userMentioned);
        System.out.println(hashTagsMentioned);

        Tweet replyTweet = new Tweet();
        replyTweet.setContent(tweetRequestDto.getContent());
        replyTweet.setAuthor(author);
        replyTweet.setInReplyTo(tweetOriginal);
        replyTweet.setMentionedUsers(userMentioned);
        replyTweet.setHashtags(hashTagsMentioned);

        tweetRepository.saveAndFlush(replyTweet);

        return tweetMapper.entityToDto(replyTweet);


    }

    @Override
    public TweetResponseDto createTweetRepost(Long id, CredentialsRequestDto credentialsRequestDto) {

        Optional<Tweet> optionalTweet = tweetRepository.findById(id);
        Optional<User> optionalUser = userRepository.findByCredentialsUsername(credentialsRequestDto.getUsername());


        if(optionalTweet.isEmpty() || optionalTweet.get().isDeleted()){
            throw new NotFoundException("No such tweet exist or it is softDeleted");
        }
        if(optionalUser.isEmpty()){
            throw  new NotFoundException("No such user Exists");
        }

        Tweet originalTweet = optionalTweet.get();
        User author = optionalUser.get();

        if(!author.getCredentials().getPassword().equals(credentialsRequestDto.getPassword())){
            throw new NotAuthorizedException("Not authorized to create a Repost");
        }

//        if(!originalTweet.getUser().getCredentials().getPassword().equals(author.getCredentials().getPassword()) ){
//            throw new NotAuthorizedException("You are not authorized to repost");
//        }


        Tweet repostTweet = new Tweet();
        repostTweet.setAuthor(author);
        repostTweet.setRepostOf(originalTweet);

        tweetRepository.saveAndFlush(repostTweet);

        return tweetMapper.entityToDto(repostTweet);

    }

    @Override
    public List<TweetResponseDto> getDirectTweetReposts(Long id) {
        Optional<Tweet> optionalTweet = tweetRepository.findById(id);
//        System.out.println(optionalTweet);
        if(optionalTweet.isEmpty() || optionalTweet.get().isDeleted()){
            throw new NotFoundException("No such tweet exist");
        }
        Tweet tweet = optionalTweet.get();
        List<Tweet> reposts =  tweet.getReposts();
        List<Tweet> responseList = new ArrayList<>();

        for (Tweet t : reposts){
            if(!t.isDeleted()){
                responseList.add(t);
            }
        }

        return tweetMapper.entitiesToDtos(responseList);
    }
    private  List<User> extractMentions(String content){
        List<User> userMentions = new ArrayList<>();
        List<String> mentions = new ArrayList<>();
        Pattern pattern = Pattern.compile("@\\w+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()){
            String mention = matcher.group();
            mentions.add(mention);
        }

        for (String username: mentions){
            String cleanedUsername = username.substring(1);
            Optional<User> userMentioned = userRepository.findByCredentialsUsername(cleanedUsername);
            if(userMentioned.isPresent()){
                userMentions.add(userMentioned.get());
            }
        }
        return userMentions;
    }
    private List<Hashtag> extractHashTag(String content) {
        List<Hashtag> hashtagsMentioned = new ArrayList<>();
        List<String> hashtagNames = new ArrayList<>();

        Pattern pattern = Pattern.compile("#\\w+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String hashTagName = matcher.group();
            hashtagNames.add(hashTagName);
        }

        for (String hashTag : hashtagNames) {
            String cleanHashTag = hashTag.substring(1); // Remove the '#' symbol
            Optional<Hashtag> hash = hashtagRepository.findHashtagsByLabel(cleanHashTag);
            if (hash.isPresent()) {
                hashtagsMentioned.add(hash.get());
            }
        }
        return hashtagsMentioned;
    }

    private List<Hashtag> addHashtagToDb(String content) {
        List<Hashtag> hashtagsMentioned = new ArrayList<>();
        List<String> hashtagNames = new ArrayList<>();

        Pattern pattern = Pattern.compile("#\\w+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String hashTagName = matcher.group();
            hashtagNames.add(hashTagName);
        }

        for(String str : hashtagNames) {
            Hashtag hashtag = new Hashtag();
            hashtag.setLabel(str.substring(1));
            Optional<Hashtag> label = hashtagRepository.findHashtagsByLabel(hashtag.getLabel());
            if(label.isEmpty()) {
                hashtagsMentioned.add(hashtag);
                hashtagRepository.saveAndFlush(hashtag);
            }
        }
        return hashtagsMentioned;
    }
}
