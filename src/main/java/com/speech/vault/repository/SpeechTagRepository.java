package com.speech.vault.repository;

import com.speech.vault.entity.SpeechTagsId;
import com.speech.vault.entity.key.SpeechTags;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpeechTagRepository extends JpaRepository<SpeechTags, SpeechTagsId> {
}
