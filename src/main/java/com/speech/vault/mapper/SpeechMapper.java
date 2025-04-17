package com.speech.vault.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.speech.vault.dto.speech.SpeechDto;
import com.speech.vault.entity.Speech;
import com.speech.vault.entity.SpeechTag;
import com.speech.vault.type.SpeechStatusType;
import com.speech.vault.util.JsonUItil;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.*;

@Mapper(componentModel = "spring")
public interface SpeechMapper {

    SpeechMapper INSTANCE = Mappers.getMapper(SpeechMapper.class);

    @Named("mapToSpeechDto")
    default SpeechDto mapToSpeechDto(Map<String, Object> map){
        String tags = (String) map.get("tags");

        return SpeechDto.builder()
                .id(map.get("id") != null ? Long.valueOf(map.get("id").toString()) : null)
                .title(map.get("title") != null ? String.valueOf(map.get("title")) : null)
                .content(map.get("content") != null ? String.valueOf(map.get("content")) : null)
                .author(map.get("author") != null ? String.valueOf(map.get("author")) : null)
                .eventAt(map.get("eventAt") != null ? (Date) map.get("eventAt") : null)
                .keywords(JsonUItil.parseJsonArrayString(tags))
                .status(map.get("status") != null ? SpeechStatusType.valueOf(String.valueOf(map.get("status"))) : null)
                .build();
    }

    @Named("stringToList")
    default List<String> stringToList(String json){
        return JsonUItil.parseJsonArrayString(json);
    }

    @IterableMapping(qualifiedByName = "mapToSpeechDto")
    List<SpeechDto> mapToDtoList(List<Map<String, Object>> list);

    Speech toSpeech(SpeechDto speechDto);

    @Mapping(source = "speech.id", target = "id")
    @Mapping(source = "speech.title", target = "title")
    @Mapping(source = "speech.content", target = "content")
    @Mapping(source = "speech.status", target = "status")
    @Mapping(source = "speech.createdBy", target = "author")
    @Mapping(source = "speech.eventAt", target = "eventAt")
    @Mapping(source = "speechTag.keywords", target = "keywords", qualifiedByName = "stringToList")
    SpeechDto toDto(Speech speech, SpeechTag speechTag);

}
