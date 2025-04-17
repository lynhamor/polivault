package com.speech.vault.util;

import com.speech.vault.dto.ResponseDto;
import com.speech.vault.dto.speech.SpeechDto;
import com.speech.vault.type.MessageKey;
import com.speech.vault.type.SpeechStatusType;
import com.speech.vault.type.StatusType;
import org.springframework.stereotype.Component;

@Component
public class SpeechUtil {

    public static ResponseDto validateSpeechDto(SpeechDto dto) {
        if(dto == null)
            return ResponseDto.builder()
                    .statusType(StatusType.ERROR)
                    .message(MessageKey.DTO_NOT_FOUND.name())
                    .build();

        if(dto.getTitle() == null || dto.getTitle().isEmpty())
            return ResponseDto.builder()
                    .statusType(StatusType.ERROR)
                    .message(MessageKey.SPEECH_DTO_TITLE_REQUIRED.name())
                    .build();

        if(dto.getAuthor() == null || dto.getAuthor().isEmpty())
            return ResponseDto.builder()
                    .statusType(StatusType.ERROR)
                    .message(MessageKey.SPEECH_DTO_AUTHOR_REQUIRED.name())
                    .build();

        if(dto.getEventAt() == null)
            return ResponseDto.builder()
                    .statusType(StatusType.ERROR)
                    .message(MessageKey.SPEECH_DTO_EVENT_DATE_REQUIRED.name())
                    .build();

        return ResponseDto.builder()
                .statusType(StatusType.SUCCESS)
                .build();

    }

    public static  MessageKey getSpeechStatusTypeMessageKey(String status){
        return switch (status.toLowerCase()){
            case "draft" -> MessageKey.SPEECH_DRAFTED_SUCCESSFULLY;
            case "delete" -> MessageKey.SPEECH_DELETED_SUCCESSFULLY;
            case "archive" -> MessageKey.SPEECH_ARCHIVED_SUCCESSFULLY;
            case "publish" -> MessageKey.SPEECH_PUBLISHED_SUCCESSFULLY;
            default -> null;
        };
    }

    public static  SpeechStatusType getSpeechStatusType(String status) {

        return switch (status.toLowerCase()){
            case "draft" -> SpeechStatusType.DRAFT;
            case "delete" -> SpeechStatusType.DELETED;
            case "archive" -> SpeechStatusType.ARCHIVED;
            case "publish" -> SpeechStatusType.PUBLISHED;
            default -> null;
        };
    }
}
