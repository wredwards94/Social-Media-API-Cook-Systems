package com.cooksys.socialmedia.controllers;
import com.cooksys.socialmedia.dtos.HashtagResponseDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.services.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("tags")
@RequiredArgsConstructor
public class HashtagController {
    private final HashtagService hashtagService;

    //Get all Tags
    @GetMapping
    public List<HashtagResponseDto> getAllTags() {
        return hashtagService.getAllTags();
    }

	//GET tags/{label}
	@GetMapping("/{label}")
	public List<TweetResponseDto> getTweetsWithHashtag(@PathVariable String label) {
		return hashtagService.getTweetsWithHashtag(label);
	}
}

