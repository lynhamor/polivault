package com.speech.vault.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.speech.vault.dto.ResponseDto;
import com.speech.vault.dto.speech.SpeechDto;
import com.speech.vault.dto.speech.SpeechesFilterDto;
import com.speech.vault.entity.SpeechTag;
import com.speech.vault.entity.Speech;
import com.speech.vault.entity.User;
import com.speech.vault.repository.SpeechTagRepository;
import com.speech.vault.repository.SpeechesRepository;
import com.speech.vault.repository.UserRepository;
import com.speech.vault.type.MessageKey;
import com.speech.vault.type.SpeechStatusType;
import com.speech.vault.type.StatusType;
import com.speech.vault.util.SpeechUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpeechService {

    private final SpeechesRepository speechesRepository;
    private final SpeechTagRepository speechTagRepository;
    private final UserRepository userRepository;

    public SpeechService(SpeechesRepository speechesRepository, SpeechTagRepository speechTagRepository, UserRepository userRepository) {
        this.speechesRepository = speechesRepository;
        this.speechTagRepository = speechTagRepository;
        this.userRepository = userRepository;
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

        Integer itemCount = speechesRepository.countAllSpeeches(filterDto.getSearch(),
                                                                filterDto.getKeywords(),
                                                                filterDto.getStatus(),
                                                                filterDto.getStartDate(),
                                                                filterDto.getEndDate());

        if(itemCount == null)
            return ResponseDto.builder()
                    .statusType(StatusType.INVALID)
                    .message(MessageKey.SPEECH_DATA_NOT_FOUND.name())
                    .build()
                    .getResponseEntity();

        int totalPage = (int) Math.ceil((double) itemCount / pageSize);

        List<Map<String, Object>> list = speechesRepository.getAllSpeeches(filterDto.getSearch(),
                                                                           filterDto.getKeywords(),
                                                                           filterDto.getStatus(),
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

    public ResponseEntity<ResponseDto> setSpeech(SpeechDto dto) throws JsonProcessingException {
        try{

            ResponseEntity<ResponseDto> validatedDto = SpeechUtil.validateSpeechDto(dto);
            if(!validatedDto.getBody().getStatusType().equals(StatusType.SUCCESS))
                return validatedDto;

            User author = userRepository.findByUsername(dto.getAuthor()).orElse(null);
            if(author == null)
                return ResponseDto.builder()
                        .statusType(StatusType.INVALID)
                        .message(MessageKey.AUTHOR_NOT_FOUND.name())
                        .build()
                        .getResponseEntity();

            Speech speech = (dto.getId() != null) ? speechesRepository.findById(dto.getId()).orElse(null) : new Speech();
            if(speech == null)
                return ResponseDto.builder()
                        .statusType(StatusType.INVALID)
                        .message(MessageKey.SPEECH_DATA_NOT_FOUND.name())
                        .build()
                        .getResponseEntity();

            Speech finalSpeech = speechBuilder(speech, dto);

            Map<String, Object> result = new HashMap<>();
            result.put("speech", finalSpeech);

            if(dto.getKeywords() != null || !dto.getKeywords().isEmpty()){

                ObjectMapper mapper = new ObjectMapper();
                String keywords = mapper.writeValueAsString(dto.getKeywords());
                SpeechTag speechTags = SpeechTag.builder()
                        .speechId(finalSpeech.getId())
                        .keywords(keywords)
                        .build();

                speechTags = speechTagRepository.save(speechTags);
                result.put("tags", speechTags);
            }

            return ResponseDto.builder()
                    .statusType(StatusType.SUCCESS)
                    .message(MessageKey.SPEECH_SAVED_SUCCESSFULLY.name())
                    .data(result)
                    .build()
                    .getResponseEntity();
        }catch (Exception e){
            return ResponseDto.builder()
                    .statusType(StatusType.INTERNAL_ERROR)
                    .message(e.getMessage())
                    .build()
                    .getResponseEntity();
        }
    }

    public ResponseEntity<ResponseDto> setSpeechStatus(String status, Long speechId) {

        SpeechStatusType speechStatusType = SpeechUtil.getSpeechStatusType(status);
        if(speechStatusType == null)
            return ResponseDto.builder()
                    .statusType(StatusType.INTERNAL_ERROR)
                    .message(MessageKey.BAD_REQUEST.name())
                    .build()
                    .getResponseEntity();

        Speech speech = speechesRepository.findById(speechId).orElse(null);
        if(speech == null)
            return ResponseDto.builder()
                    .statusType(StatusType.INVALID)
                    .message(MessageKey.SPEECH_DATA_NOT_FOUND.name())
                    .build()
                    .getResponseEntity();

        speech.setStatus(speechStatusType);
        speech.setIsDeleted(true);
        speechesRepository.save(speech);

        return ResponseDto.builder()
                .statusType(StatusType.SUCCESS)
                .message(SpeechUtil.getSpeechStatusTypeMessageKey(status).name())
                .build()
                .getResponseEntity();
    }

    private Speech speechBuilder(Speech speech, SpeechDto dto) {

        speech.setTitle(dto.getTitle());
        speech.setSlug(dto.getTitle().replace(" ","-"));
        speech.setContent(dto.getContent());
        speech.setStatus(dto.getStatus());
        speech.setEventAt(dto.getEventAt());
        if(speech.getId() == null){
            speech.setCreatedAt(new Date());
            speech.setCreatedBy(dto.getAuthor());
        }
        speech.setUpdatedAt(new Date());
        speech.setUpdatedBy(dto.getAuthor());
        speech.setIsDeleted(false);

        return speechesRepository.save(speech);
    }

}
