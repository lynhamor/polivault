package com.speech.vault.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "speech_tag")
public class SpeechTag {

    @Id
    @Column(name = "speech_id", nullable = false)
    private Integer speechId;

    @Lob
    @Column(name = "keywords", nullable = false)
    private String keywords;

}
