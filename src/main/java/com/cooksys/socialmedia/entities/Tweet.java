package com.cooksys.socialmedia.entities;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@NoArgsConstructor
@Data
public class Tweet {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "tweet_hashtags",
			joinColumns = @JoinColumn(name = "tweet_id"),
			inverseJoinColumns = @JoinColumn(name = "hashtag_id")
	)
	private List<Hashtag> hashtags;

	@ManyToMany(mappedBy = "likedTweets", fetch = FetchType.EAGER)
	private List<User> likedByUsers;
	
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_mentions",
            joinColumns = @JoinColumn(name = "tweet_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> mentionedUsers;

	@ManyToOne
	@JoinColumn(name = "author")
	private User author;

	@Column(nullable = false)
	@CreationTimestamp
	private Timestamp postedTime;

	private boolean deleted;

	private String content;

	@ManyToOne
	@JoinColumn(name = "inReplyToId")
	private Tweet inReplyTo;

	@ManyToOne
	@JoinColumn(name = "repostOfId")
	private Tweet repostOf;

	@OneToMany(mappedBy = "inReplyTo")
	private List<Tweet> replies = new ArrayList<>();

	@OneToMany(mappedBy = "repostOf")
	private List<Tweet> reposts = new ArrayList<>();

	@Override
	public String toString() {
		return "Tweet [id=" + id + ", hashtags=" + hashtags + ", likedByUsers=" + likedByUsers + ", mentionedUsers="
				+ mentionedUsers + ", user=" + author + ", posted=" + postedTime + ", deleted=" + deleted + ", content="
				+ content + ", inReplyTo=" + inReplyTo + ", repostOf=" + repostOf + "]";
	}
}