package com.speech.vault.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.speech.vault.type.StatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto {

    private StatusType statusType;
    private String message;
    private Object data;
    private Long timeMills;


    @JsonIgnore
    public ResponseEntity<ResponseDto> getResponseEntity() {
        this.timeMills = System.currentTimeMillis();
        HttpStatus httpStatus = switch (statusType) {
            case SUCCESS, WARNING, INFO -> HttpStatus.OK;
            case ERROR, INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case INVALID -> HttpStatus.BAD_REQUEST;
        };
        return new ResponseEntity<>(this, httpStatus);
    }

    @JsonIgnore
    public ResponseEntity<Object> getResponseEntityOnlyData() {
        HttpStatus httpStatus = switch (statusType) {
            case SUCCESS, WARNING, INFO -> HttpStatus.OK;
            case ERROR, INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case INVALID -> HttpStatus.BAD_REQUEST;
        };
        return new ResponseEntity<>(this.data, httpStatus);
    }
}
