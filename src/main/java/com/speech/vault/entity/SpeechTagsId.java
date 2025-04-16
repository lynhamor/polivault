package com.speech.vault.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class SpeechTagsId implements java.io.Serializable {
    private static final long serialVersionUID = 8070720277498517121L;
    @Column(name = "speech_id", nullable = false)
    private Integer speechId;

    @Column(name = "keywords", nullable = false, length = 100)
    private String keywords;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SpeechTagsId entity = (SpeechTagsId) o;
        return Objects.equals(this.keywords, entity.keywords) &&
                Objects.equals(this.speechId, entity.speechId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keywords, speechId);
    }

}