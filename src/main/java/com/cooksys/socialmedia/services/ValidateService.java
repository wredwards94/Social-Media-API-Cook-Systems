package com.cooksys.socialmedia.services;

import org.springframework.http.ResponseEntity;

public interface ValidateService {

    boolean checkAvailableUsername(String username);

	boolean checkIfUsernameExists(String username);

    ResponseEntity<Boolean> tagExistsByLabel(String label);
}
