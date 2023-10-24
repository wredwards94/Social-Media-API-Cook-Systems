package com.cooksys.socialmedia.mappers;

import com.cooksys.socialmedia.dtos.CredentialsRequestDto;
import com.cooksys.socialmedia.dtos.CredentialsResponseDto;
import com.cooksys.socialmedia.entities.Credentials;
import org.mapstruct.Mapper;

import java.util.List;
@Mapper(componentModel = "spring")
public interface CredentialsMapper {

    CredentialsResponseDto entityToDto(Credentials credentials);

    Credentials requestDtoToEntity(CredentialsRequestDto credentialsRequestDto);

    List<Credentials> entitiesToDtos(List<CredentialsRequestDto> credentialsRequestDtos);
}
