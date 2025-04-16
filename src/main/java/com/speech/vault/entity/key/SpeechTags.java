package com.speech.vault.entity.key;

import com.speech.vault.entity.SpeechTagsId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "speech_tags")
public class SpeechTags {
    @EmbeddedId
    private SpeechTagsId id;

}
