package com.speech.vault.dto.speech;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.speech.vault.type.SpeechStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpeechDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("content")
    private String content;

    @JsonProperty("status")
    private SpeechStatusType status;

    @JsonProperty("author")
    private String author;

    @JsonProperty("keywords")
    private List<String> keywords;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date eventAt;
}
