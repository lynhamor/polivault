package com.speech.vault.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.speech.vault.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserFilterDto {

    @JsonProperty("userType")
    private List<UserType> userType;

    @JsonProperty("page")
    private int page;

    @JsonProperty("size")
    private int pageSize;
}
