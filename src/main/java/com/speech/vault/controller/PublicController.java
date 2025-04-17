package com.speech.vault.controller;

import com.speech.vault.dto.speech.SpeechDto;
import com.speech.vault.service.SpeechService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/public")
public class PublicController {

    private final SpeechService speechService;

    public PublicController(SpeechService speechService) {
        this.speechService = speechService;
    }

    @GetMapping(value = {"/", ""})
    public ModelAndView index(ModelAndView modelAndView) {
        modelAndView.setViewName("index");

        return modelAndView;
    }

    @GetMapping("/shared/speech/{id}/{slug}")
    public ModelAndView getSharedSpeech(@PathVariable Long id,
                                  @PathVariable String slug,
                                  ModelAndView modelAndView) {
        SpeechDto data = speechService.getSharedSpeech(id, slug);
        modelAndView.addObject("data", data);
        modelAndView.setViewName("speech");

        return modelAndView;
    }
}
