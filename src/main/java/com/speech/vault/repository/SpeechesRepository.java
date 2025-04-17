package com.speech.vault.repository;

import com.speech.vault.entity.Speech;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SpeechesRepository extends JpaRepository<Speech, Long> {

    @Query(value = """
        SELECT
            s.id                     AS id,
            s.title                  AS title,
            s.content                AS content,
            u.username               AS username,
            u.name                   AS author,
            s.status                 AS status,
            s.slug                   AS slug,
            s.event_at               AS eventAt,
            s.is_deleted             AS isDeleted,
            st.keywords              AS tags,
            s.created_at             AS createdAt,
            s.created_by             AS createdBy,
            s.updated_at             AS updatedAt,
            s.updated_by             AS updatedBy
        FROM
            `speech` AS s
        LEFT JOIN
            `speech_tag` AS st
            ON s.id = st.speech_id
        LEFT JOIN
            `user` AS u
            ON u.username = s.created_by
        WHERE
            (
                :search IS NULL OR :search = ''
                OR (
                    s.title LIKE CONCAT('%', :search, '%')
                    OR s.content LIKE CONCAT('%', :search, '%')
                    OR s.created_by LIKE CONCAT('%', :search, '%')
                    OR s.updated_by LIKE CONCAT('%', :search, '%')
                    OR st.keywords LIKE CONCAT('%', :search, '%')
                )
            )
            AND (:#{#status == null || #status.isEmpty()} = TRUE OR s.status IN (:status))
            AND (:#{#keywords == null || #keywords.isEmpty()} = TRUE OR JSON_CONTAINS(st.keywords, JSON_QUOTE(:keywords)))
            AND (
                   (COALESCE(:startDate, NULL) IS NULL OR COALESCE(:endDate, NULL) IS NULL)
                OR s.event_at BETWEEN :startDate AND :endDate
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
            `speech` AS s
        LEFT JOIN
            `speech_tag` AS st
            ON s.id = st.speech_id
        LEFT JOIN
            `user` AS u
            ON u.username = s.created_by
        WHERE
            (
                :search IS NULL OR :search = ''
                OR (
                    s.title LIKE CONCAT('%', :search, '%')
                    OR s.content LIKE CONCAT('%', :search, '%')
                    OR s.created_by LIKE CONCAT('%', :search, '%')
                    OR s.updated_by LIKE CONCAT('%', :search, '%')
                    OR st.keywords LIKE CONCAT('%', :search, '%')
                )
            )
            AND (:#{#status == null || #status.isEmpty()} = TRUE OR s.status IN (:status))
            AND (:#{#keywords == null || #keywords.isEmpty()} = TRUE OR JSON_CONTAINS(st.keywords, JSON_QUOTE(:keywords)))
            AND (
                   (COALESCE(:startDate, NULL) IS NULL OR COALESCE(:endDate, NULL) IS NULL)
                OR s.event_at BETWEEN :startDate AND :endDate
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
