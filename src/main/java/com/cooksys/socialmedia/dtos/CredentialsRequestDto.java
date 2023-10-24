package com.cooksys.socialmedia.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CredentialsRequestDto {
    @NotNull(message = "username cannot be blank")
    private String username;

    @NotNull(message = "password cannot be blank")
    private String password;
}
