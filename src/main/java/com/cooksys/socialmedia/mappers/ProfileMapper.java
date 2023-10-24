package com.cooksys.socialmedia.mappers;

import com.cooksys.socialmedia.dtos.ProfileRequestDto;
import com.cooksys.socialmedia.dtos.ProfileResponseDto;
import com.cooksys.socialmedia.entities.Profile;
import org.mapstruct.Mapper;

import java.util.List;
@Mapper(componentModel = "spring")
public interface ProfileMapper {

    ProfileResponseDto entityToDto(Profile profile);

    Profile requestDtoToEntity(ProfileRequestDto requestDto);

    List<ProfileResponseDto> entitiesToDtos(List<Profile> profiles);

}
