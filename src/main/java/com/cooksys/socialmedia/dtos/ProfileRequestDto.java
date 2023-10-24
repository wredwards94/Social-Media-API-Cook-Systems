package com.cooksys.socialmedia.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileRequestDto {

    private String firstName;

    private String lastName;

    private String email;

    private String phone;
}