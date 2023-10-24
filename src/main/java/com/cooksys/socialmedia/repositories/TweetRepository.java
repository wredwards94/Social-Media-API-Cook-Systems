package com.cooksys.socialmedia.repositories;

import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.entities.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {
    List<Tweet> findByDeletedIsFalseOrderByPostedTimeDesc();

    List<Tweet> findByAuthorAndDeletedIsFalseOrderByPostedTimeDesc(User username);
}
