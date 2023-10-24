package com.cooksys.socialmedia.dtos;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ProfileResponseDto {

    private String firstName;

    private String lastName;

    private String email;

    private String phone;
}