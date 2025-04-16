package com.speech.vault.controller;

import com.speech.vault.dto.ResponseDto;
import com.speech.vault.dto.user.UserFilterDto;
import com.speech.vault.entity.User;
import com.speech.vault.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDto> register(@RequestBody User dto) {
        return userService.register(dto);
    }

    @PostMapping("/list")
    public ResponseEntity<ResponseDto> register(@RequestBody UserFilterDto filterDto) {
        return userService.getAllUser(filterDto);
    }
}
