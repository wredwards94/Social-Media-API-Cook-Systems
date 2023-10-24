package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.entities.Hashtag;
import com.cooksys.socialmedia.repositories.HashtagRepository;
import com.cooksys.socialmedia.services.ValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService {
    private final UserRepository userRepository;
    private final HashtagRepository hashtagRepository;

    @Override
    public ResponseEntity<Boolean> tagExistsByLabel(String label) {
        Optional<Hashtag> optionalHashtag = hashtagRepository.findHashtagByLabel(label);
        boolean exists = optionalHashtag.isPresent();
        return ResponseEntity.ok(exists);
    }

    @Override
    public boolean checkAvailableUsername(String username) {
        Optional<User> optionalUser = userRepository.findByCredentialsUsername(username);
        return optionalUser.isEmpty();
    }

    @Override
    public boolean checkIfUsernameExists(String username) {
        Optional<User> optionalUser = userRepository.findByCredentialsUsername(username);
        if(optionalUser.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
}
