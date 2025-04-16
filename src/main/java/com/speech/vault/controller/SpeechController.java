package com.speech.vault.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.speech.vault.dto.ResponseDto;
import com.speech.vault.dto.speech.SpeechDto;
import com.speech.vault.dto.speech.SpeechesFilterDto;
import com.speech.vault.service.SpeechService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/speech")
public class SpeechController {

    private final SpeechService speechService;

    public SpeechController(SpeechService speechService) {
        this.speechService = speechService;
    }

    @PostMapping("/list")
    public ResponseEntity<ResponseDto> getAllSpeech(@RequestBody SpeechesFilterDto filterDto){
        return speechService.getAllSpeeches(filterDto);
    }
    
    @PostMapping("/set")
    public ResponseEntity<ResponseDto> setSpeech(@RequestBody SpeechDto dto) throws JsonProcessingException {
        return speechService.setSpeech(dto);
    }

    @PostMapping("/delete/{speechId}")
    public ResponseEntity<ResponseDto> deleteSpeech(@PathVariable Long speechId) throws JsonProcessingException {
        return speechService.deleteSpeech(speechId);
    }
}
