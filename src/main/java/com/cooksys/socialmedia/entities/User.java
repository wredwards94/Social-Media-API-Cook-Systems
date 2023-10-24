package com.cooksys.socialmedia.entities;

import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name="user_table")
@NoArgsConstructor
@Data
public class User {
	@Id
	@GeneratedValue
	private Long id;
	
	@OneToMany(mappedBy= "author")
	private List<Tweet> tweets;
	
	@Embedded
	private Profile profile;
	
	@Embedded
	private Credentials credentials;

	@CreationTimestamp
	private Timestamp joined;
	
	private boolean deleted;
	
    @ManyToMany(mappedBy = "mentionedUsers")
    private List<Tweet> mentionedInTweets;
    
	@ManyToMany
	@JoinTable(
			name = "user_likes",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "tweet_id")
	)
	private List<Tweet> likedTweets;
	
    @ManyToMany
    @JoinTable(
            name = "followers_following",
            joinColumns = @JoinColumn(name = "following_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    private List<User> followers;

    @ManyToMany(mappedBy = "followers")
    private List<User> following;

	@Override
	public String toString() {
		return "User [id=" + id + ", tweets=" + tweets + ", profile=" + profile + ", credentials=" + credentials
				+ ", joined=" + joined + ", deleted=" + deleted + ", mentionedInTweets=" + mentionedInTweets
				+ ", likedTweets=" + likedTweets + ", followers=" + followers + ", following=" + following + "]";
	}
}
