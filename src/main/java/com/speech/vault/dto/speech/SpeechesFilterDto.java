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
public class SpeechesFilterDto {

    @JsonProperty("search")
    private String search;

    @JsonProperty("keywords")
    private List<String> keywords;

    @JsonProperty("status")
    private List<SpeechStatusType> status;

    @JsonProperty("startDate")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;

    @JsonProperty("endDate")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    @JsonProperty("page")
    private int page;

    @JsonProperty("size")
    private int pageSize;
}
