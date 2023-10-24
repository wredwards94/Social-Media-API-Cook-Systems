package com.cooksys.socialmedia.controllers;

import com.cooksys.socialmedia.services.ValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/validate")
@RequiredArgsConstructor
public class ValidateController {
    private final ValidateService validateService;

    /*
    GET validate/username/available/@{username}
    Checks whether or not a given username is available.
    Response:  boolean
    */

    @GetMapping("/tag/exists/{label}")
    public ResponseEntity<Boolean> tagExistsByLabel(@PathVariable String label){
        return validateService.tagExistsByLabel(label);
    }

    @GetMapping("/username/available/@{username}")
    public boolean checkAvailableUsername(@PathVariable String username) {
        return validateService.checkAvailableUsername(username);
    }
	//GET validate/username/exists/@{username}
	@GetMapping("/username/exists/@{username}")
	public boolean checkIfUsernameExists(@PathVariable String username) {
		return validateService.checkIfUsernameExists(username);
	}
}
