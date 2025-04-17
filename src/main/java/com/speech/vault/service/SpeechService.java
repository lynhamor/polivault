package com.speech.vault.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.speech.vault.dto.ResponseDto;
import com.speech.vault.dto.speech.SpeechDto;
import com.speech.vault.dto.speech.SpeechesFilterDto;
import com.speech.vault.entity.SpeechTag;
import com.speech.vault.entity.Speech;
import com.speech.vault.entity.User;
import com.speech.vault.mapper.SpeechMapper;
import com.speech.vault.repository.SpeechTagRepository;
import com.speech.vault.repository.SpeechesRepository;
import com.speech.vault.repository.UserRepository;
import com.speech.vault.type.MessageKey;
import com.speech.vault.type.SpeechStatusType;
import com.speech.vault.type.StatusType;
import com.speech.vault.util.SpeechUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;

@Service
public class SpeechService {

    private final SpeechesRepository speechRepository;
    private final SpeechTagRepository speechTagRepository;
    private final UserRepository userRepository;

    @Autowired
    private SpeechMapper speechMapper;

    public SpeechService(SpeechesRepository speechesRepository, SpeechTagRepository speechTagRepository,
                         UserRepository userRepository
    ) {
        this.speechRepository = speechesRepository;
        this.speechTagRepository = speechTagRepository;
        this.userRepository = userRepository;
    }

    public SpeechDto getSharedSpeech(Long id, String slug) {

        Map<String, Object> result = speechRepository.getSharedSpeech(id, slug).orElse(null);

        if(result == null || result.isEmpty())
            return null;

        return speechMapper.mapToSpeechDto(result);
    }

    public ResponseEntity<ResponseDto> shareSpeech(Long speechId) {

        Map<String, Object> result = speechRepository.getSharedSpeech(speechId, null).orElse(null);
        if(result == null || result.isEmpty())
            return ResponseDto.builder()
                    .statusType(StatusType.INVALID)
                    .message(MessageKey.SPEECH_DATA_NOT_FOUND.name())
                    .build()
                    .getResponseEntity();

        SpeechDto speechDto = speechMapper.mapToSpeechDto(result);

        String baseUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .build()
                .toUriString();

        StringBuilder sb = new StringBuilder();
        String sharedUrl = sb.append(baseUrl)
                .append("/public/shared/speech/")
                .append(speechDto.getId())
                .append("/")
                .append(speechDto.getSlug())
                .toString();


        return ResponseDto.builder()
                .statusType(StatusType.SUCCESS)
                .message(MessageKey.SUCCESS.name())
                .data(sharedUrl)
                .build()
                .getResponseEntity();
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

        Integer itemCount = speechRepository.countAllSpeeches(filterDto.getSearch(),
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

        List<Map<String, Object>> list = speechRepository.getAllSpeeches(filterDto.getSearch(),
                                                                           filterDto.getKeywords(),
                                                                           filterDto.getStatus(),
                                                                           filterDto.getStartDate(),
                                                                           filterDto.getEndDate(),
                                                                           nOffset,
                                                                           pageSize);
        List<SpeechDto> dtoList = speechDtoBuilder(list);

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("page", Math.max(page, 1));
        pagination.put("totalPage", totalPage);
        pagination.put("count",  dtoList.size());
        pagination.put("size", pageSize);

        return ResponseDto.builder()
                .statusType(StatusType.SUCCESS)
                .message(MessageKey.SPEECH_FETCHED_SUCCESSFULLY.name())
                .data(Map.of(
                        "list", dtoList,
                        "pagination", pagination
                ))
                .build()
                .getResponseEntity();
    }

    public ResponseEntity<ResponseDto> setSpeech(SpeechDto dto) {
        try{

            if(dto.getId() == null){

                ResponseEntity<ResponseDto> validatedDto = SpeechUtil.validateSpeechDto(dto);
                if(!validatedDto.getBody().getStatusType().equals(StatusType.SUCCESS))
                    return validatedDto;
            }

            Speech speech = (dto.getId() != null) ? speechRepository.findById(dto.getId()).orElse(null) : new Speech();
            if(speech == null)
                return ResponseDto.builder()
                        .statusType(StatusType.INVALID)
                        .message(MessageKey.SPEECH_DATA_NOT_FOUND.name())
                        .build()
                        .getResponseEntity();

            User author = userRepository.findByUsername(dto.getAuthor()).orElse(null);
            if(author == null)
                return ResponseDto.builder()
                        .statusType(StatusType.INVALID)
                        .message(MessageKey.AUTHOR_NOT_FOUND.name())
                        .build()
                        .getResponseEntity();

            Speech finalSpeech = speechBuilder(speech, dto);

            SpeechTag speechTag = speechTagBuilder(dto, finalSpeech);

            SpeechDto responseData = speechMapper.toDto(finalSpeech, speechTag);

            return ResponseDto.builder()
                    .statusType(StatusType.SUCCESS)
                    .message(MessageKey.SPEECH_SAVED_SUCCESSFULLY.name())
                    .data(responseData)
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

        Speech speech = speechRepository.findById(speechId).orElse(null);
        if(speech == null)
            return ResponseDto.builder()
                    .statusType(StatusType.INVALID)
                    .message(MessageKey.SPEECH_DATA_NOT_FOUND.name())
                    .build()
                    .getResponseEntity();

        speech.setStatus(speechStatusType);
        speech.setIsDeleted(true);
        speechRepository.save(speech);

        return ResponseDto.builder()
                .statusType(StatusType.SUCCESS)
                .message(SpeechUtil.getSpeechStatusTypeMessageKey(status).name())
                .build()
                .getResponseEntity();
    }

    private Speech speechBuilder(Speech speech, SpeechDto dto) {

        speech = speechMapper.toSpeech(dto);

        speech.setSlug(dto.getTitle().replace(" ","-"));

        if(speech.getId() == null){
            speech.setCreatedAt(new Date());
            speech.setCreatedBy(dto.getAuthor());
        }
        speech.setUpdatedAt(new Date());
        speech.setUpdatedBy(dto.getAuthor());
        speech.setIsDeleted(false);

        return speechRepository.save(speech);
    }

    private SpeechTag speechTagBuilder(SpeechDto dto, Speech speech) throws JsonProcessingException {
        if(dto.getKeywords() != null || !dto.getKeywords().isEmpty()){

            ObjectMapper mapper = new ObjectMapper();
            String keywords = mapper.writeValueAsString(dto.getKeywords());
            SpeechTag speechTags = SpeechTag.builder()
                    .speechId(speech.getId())
                    .keywords(keywords)
                    .build();

            return speechTagRepository.save(speechTags);
        }
        return null;
    }

    private List<SpeechDto> speechDtoBuilder(List<Map<String, Object>> mapList){
        return speechMapper.mapToDtoList(mapList);
    }
}
