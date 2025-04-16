package com.speech.vault.service;

import com.speech.vault.dto.ResponseDto;
import com.speech.vault.dto.speech.SpeechDto;
import com.speech.vault.dto.speech.SpeechesFilterDto;
import com.speech.vault.entity.SpeechTagsId;
import com.speech.vault.entity.Speeches;
import com.speech.vault.entity.key.SpeechTags;
import com.speech.vault.repository.SpeechTagRepository;
import com.speech.vault.repository.SpeechesRepository;
import com.speech.vault.type.MessageKey;
import com.speech.vault.type.StatusType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpeechService {

    private final SpeechesRepository speechesRepository;
    private final SpeechTagRepository speechTagRepository;

    public SpeechService(SpeechesRepository speechesRepository, SpeechTagRepository speechTagRepository) {
        this.speechesRepository = speechesRepository;
        this.speechTagRepository = speechTagRepository;
    }

    public ResponseEntity<ResponseDto> getAllSpeeches(SpeechesFilterDto filterDto) {

        if(filterDto == null)
            return ResponseDto.builder()
                    .statusType(StatusType.ERROR)
                    .message(MessageKey.DTO_NOT_FOUND.name())
                    .build()
                    .getResponseEntity();

        int page = filterDto.getPage();
        int pageSize = filterDto.getPageSize();
        int nOffset = Math.max(page - 1, 0) * pageSize;

        int itemCount = speechesRepository.countAllSpeeches(filterDto.getSearch(),
                                                            filterDto.getKeywords(),
                                                            filterDto.getStartDate(),
                                                            filterDto.getEndDate());

        int totalPage = (int) Math.ceil((double) itemCount / pageSize);

        List<Map<String, Object>> list = speechesRepository.getAllSpeeches(filterDto.getSearch(),
                                                                           filterDto.getKeywords(),
                                                                           filterDto.getStartDate(),
                                                                           filterDto.getEndDate(),
                                                                           nOffset,
                                                                           pageSize);

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("page", Math.max(page, 1));
        pagination.put("totalPage", totalPage);
        pagination.put("count",  list.size());
        pagination.put("size", pageSize);

        return ResponseDto.builder()
                .statusType(StatusType.SUCCESS)
                .message(MessageKey.SPEECH_FETCHED_SUCCESSFULLY.name())
                .data(Map.of(
                        "list", list,
                        "pagination", pagination
                ))
                .build()
                .getResponseEntity();
    }

    public ResponseEntity<ResponseDto> setSpeech(SpeechDto dto) {

        ResponseEntity<ResponseDto> validatedDto = validateSpeechDto(dto);
        if(!validatedDto.getBody().getStatusType().equals(StatusType.SUCCESS))
            return validatedDto;

        Speeches speech = (dto.getId() != null) ? speechesRepository.findById(dto.getId()).orElse(new Speeches()) : new Speeches();
        speech.setTitle(dto.getTitle());
        speech.setSlug(dto.getTitle().replace(" ","-"));
        speech.setContent(dto.getContent());
        speech.setStatus(dto.getStatus());
        speech.setEventAt(dto.getEventAt());
        speech.setCreatedAt(new Date());
        speech.setCreatedBy(dto.getAuthor());
        speech.setUpdatedAt(new Date());
        speech.setUpdatedBy(dto.getAuthor());
        speech.setIsDeleted(false);
        speech = speechesRepository.save(speech);

        Map<String, Object> result = new HashMap<>();
        result.put("speech", speech);

        if(dto.getKeywords() != null || !dto.getKeywords().isEmpty()){
            Speeches finalSpeech = speech;
            List<SpeechTags> speechTagsIdList = dto.getKeywords().stream()
                    .map(tags -> new SpeechTags(SpeechTagsId.builder()
                            .speechId(finalSpeech.getId())
                            .keywords(tags)
                            .build()
                    ))
                    .toList();

            speechTagsIdList = speechTagRepository.saveAll(speechTagsIdList);
            result.put("tags", speechTagsIdList);
        }

        return ResponseDto.builder()
                .statusType(StatusType.SUCCESS)
                .message(MessageKey.SPEECH_SAVED_SUCCESSFULLY.name())
                .data(result)
                .build()
                .getResponseEntity();
    }

    private ResponseEntity<ResponseDto> validateSpeechDto(SpeechDto dto) {
        if(dto == null)
            return ResponseDto.builder()
                    .statusType(StatusType.ERROR)
                    .message(MessageKey.DTO_NOT_FOUND.name())
                    .build()
                    .getResponseEntity();

        if(dto.getTitle() == null || dto.getTitle().isEmpty())

            return ResponseDto.builder()
                    .statusType(StatusType.ERROR)
                    .message(MessageKey.SPEECH_DTO_TITLE_REQUIRED.name())
                    .build()
                    .getResponseEntity();

        if(dto.getAuthor() == null || dto.getAuthor().isEmpty())
            return ResponseDto.builder()
                    .statusType(StatusType.ERROR)
                    .message(MessageKey.SPEECH_DTO_AUTHOR_REQUIRED.name())
                    .build()
                    .getResponseEntity();

        if(dto.getEventAt() == null)
            return ResponseDto.builder()
                    .statusType(StatusType.ERROR)
                    .message(MessageKey.SPEECH_DTO_EVENT_DATE_REQUIRED.name())
                    .build()
                    .getResponseEntity();

        return ResponseDto.builder()
                .statusType(StatusType.SUCCESS)
                .build()
                .getResponseEntity();

    }
}
