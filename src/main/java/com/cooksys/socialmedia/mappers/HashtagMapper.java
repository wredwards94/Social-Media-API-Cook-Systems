package com.cooksys.socialmedia.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.cooksys.socialmedia.dtos.HashtagRequestDto;
import com.cooksys.socialmedia.dtos.HashtagResponseDto;
import com.cooksys.socialmedia.entities.Hashtag;

@Mapper(componentModel = "spring")
public interface HashtagMapper {

	HashtagResponseDto entityToDto(Hashtag hashtag);
	
	Hashtag requestDtoToEntity(HashtagRequestDto hashtagRequestDto);
	
	List<HashtagResponseDto> entitiesToDtos(List<Hashtag> hashtags);
}
