package com.speech.vault.controller;

import com.speech.vault.dto.ResponseDto;
import com.speech.vault.dto.speech.SpeechDto;
import com.speech.vault.dto.speech.SpeechesFilterDto;
import com.speech.vault.service.SpeechService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ResponseDto> setSpeech(@RequestBody SpeechDto dto){
        return speechService.setSpeech(dto);
    }
}
