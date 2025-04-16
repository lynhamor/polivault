package com.speech.vault.service;

import com.speech.vault.dto.ResponseDto;
import com.speech.vault.dto.user.UserFilterDto;
import com.speech.vault.entity.User;
import com.speech.vault.repository.UserRepository;
import com.speech.vault.type.MessageKey;
import com.speech.vault.type.StatusType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<ResponseDto> register(User dto) {

        if(dto == null)
            return ResponseDto.builder()
                    .statusType(StatusType.ERROR)
                    .message(MessageKey.DTO_NOT_FOUND.name())
                    .build()
                    .getResponseEntity();

        User user = userRepository.findByUsername(dto.getUsername()).orElse(null);
        if (user != null)
            return ResponseDto.builder()
                    .statusType(StatusType.ERROR)
                    .message(MessageKey.USER_ALREADY_EXIST.name())
                    .build()
                    .getResponseEntity();

        user = userBuilder(user, dto);

        return ResponseDto.builder()
                .statusType(StatusType.SUCCESS)
                .message(MessageKey.USER_REGISTERED_SUCCESSFULLY.name())
                .data(user)
                .build()
                .getResponseEntity();
    }

    private User userBuilder(User user, User dto) {
        user = new User();
        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        user.setUserType(dto.getUserType());
        user.setCreatedAt(new Date());

        return userRepository.save(user);
    }

    public ResponseEntity<ResponseDto> getAllUser(UserFilterDto filterDto) {

        if(filterDto == null)
            return ResponseDto.builder()
                    .statusType(StatusType.ERROR)
                    .message(MessageKey.DTO_NOT_FOUND.name())
                    .build()
                    .getResponseEntity();

        int page = filterDto.getPage();
        int pageSize = filterDto.getPageSize();
        int nOffset = Math.max(page - 1, 0) * pageSize;

        Integer itemCount = userRepository.countAllUser(filterDto.getUserType());

        if(itemCount == null)
            return ResponseDto.builder()
                    .statusType(StatusType.INVALID)
                    .message(MessageKey.USER_DATA_NOT_FOUND.name())
                    .build()
                    .getResponseEntity();

        int totalPage = (int) Math.ceil((double) itemCount / pageSize);

        List<Map<String, Object>> list = userRepository.getAllUser(filterDto.getUserType(),
                                                                   nOffset,
                                                                   pageSize);

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("page", Math.max(page, 1));
        pagination.put("totalPage", totalPage);
        pagination.put("count",  list.size());
        pagination.put("size", pageSize);

        return ResponseDto.builder()
                .statusType(StatusType.SUCCESS)
                .message(MessageKey.USER_FETCHED_SUCCESSFULLY.name())
                .data(Map.of(
                        "list", list,
                        "pagination", pagination
                ))
                .build()
                .getResponseEntity();
    }


}
