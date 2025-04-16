package com.speech.vault.repository;

import com.speech.vault.entity.Speeches;
import com.speech.vault.type.SpeechStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SpeechesRepository extends JpaRepository<Speeches, Long> {

    @Query(value = """
        SELECT
            s.id                     AS id,
            s.title                  AS title,
            s.content                AS content,
            s.created_by             AS author,
            s.status                 AS status,
            s.slug                   AS slug,
            s.event_at               AS eventAt,
            s.is_deleted             AS isDeleted,
            st.keywords              AS tags,
            s.created_at             AS created_at
        FROM
            `speeches` AS s
        LEFT JOIN
            `speech_tag` AS st ON s.id = st.speech_id
        WHERE
            (
                :search IS NULL OR :search = ''
                OR (
                    s.title LIKE CONCAT('%', :search, '%')
                    OR s.content LIKE CONCAT('%', :search, '%')
                    OR s.created_by LIKE CONCAT('%', :search, '%')
                    OR st.keywords LIKE CONCAT('%', :search, '%')
                )
            )
            AND (:#{#status == null || #status.isEmpty()} = TRUE OR s.status IN (:status))
            AND (:#{#keywords == null || #keywords.isEmpty()} = TRUE OR JSON_CONTAINS(st.keywords, JSON_QUOTE(:keywords)))
            AND (
                COALESCE(:startDate, :endDate) IS NULL
                OR s.created_at BETWEEN :startDate AND :endDate
            )
        GROUP BY
            s.id
        LIMIT :nOffset, :nLimit
    """,nativeQuery = true)
    List<Map<String, Object>> getAllSpeeches(@Param("search") String search,
                                             @Param("keywords") List<String> keywords,
                                             @Param("status") List<String> status,
                                             @Param("startDate") Date startDate,
                                             @Param("endDate") Date endDate,
                                             int nOffset,
                                             int nLimit);

    @Query(value = """
        SELECT
            COUNT(1)
        FROM
            `speeches` AS s
        LEFT JOIN
            `speech_tag` AS st ON s.id = st.speech_id
        WHERE
            (
                :search IS NULL OR :search = ''
                OR (
                    s.title LIKE CONCAT('%', :search, '%')
                    OR s.content LIKE CONCAT('%', :search, '%')
                    OR s.created_by LIKE CONCAT('%', :search, '%')
                    OR st.keywords LIKE CONCAT('%', :search, '%')
                )
            )
            AND (:#{#status == null || #status.isEmpty()} = TRUE OR s.status IN (:status))
            AND (:#{#keywords == null || #keywords.isEmpty()} = TRUE OR JSON_CONTAINS(st.keywords, JSON_QUOTE(:keywords)))
            AND (
                COALESCE(:startDate, :endDate) IS NULL
                OR s.created_at BETWEEN :startDate AND :endDate
            )
        GROUP BY
            s.id
    """,nativeQuery = true)
    Integer countAllSpeeches(@Param("search") String search,
                             @Param("keywords") List<String> keywords,
                             @Param("status") List<String> status,
                             @Param("startDate") Date startDate,
                             @Param("endDate") Date endDate);
}
