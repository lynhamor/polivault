package com.speech.vault.repository;

import com.speech.vault.entity.Speeches;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SpeechesRepository extends JpaRepository<Speeches, Long> {

    @Query(value = """
        SELECT
            s.id                AS id,
            s.title             AS title,
            s.content           AS content,
            s.created_by        as author,
            s.status            AS status,
            s.slug              AS slug,
            s.event_at          AS eventAt,
            s.is_deleted        AS isDeleted,
            JSON_ARRAYAGG(st.keywords) AS tags,
            s.created_at        as created_at
        FROM
            `speeches` AS s
        LEFT JOIN
            `speech_tags` AS st
            ON st.speech_id = s.id
        WHERE
                (:#{#keywords == null || #keywords.isEmpty()} = TRUE OR st.keywords IN (:keywords))
            AND (
                    (:search IS NULL OR :search = '')
                    OR(
                           s.title LIKE CONCAT('%', :search, '%')
                        OR s.content LIKE CONCAT('%', :search, '%')
                        OR s.created_by LIKE CONCAT('%', :search, '%')
                        OR st.keywords LIKE CONCAT('%', :search, '%')
                    )  
                )
            AND ((COALESCE(:startDate, :endDate) IS NULL) OR s.created_at BETWEEN :startDate AND :endDate)
        LIMIT :nOffset, :nLimit
    """,nativeQuery = true)
    List<Map<String, Object>> getAllSpeeches(@Param("search") String search,
                                             @Param("keywords") List<String> keywords,
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
            `speech_tags` AS st
            ON st.speech_id = s.id
        WHERE
                (:#{#keywords == null || #keywords.isEmpty()} = TRUE OR st.keywords IN (:keywords))
            AND (
                    (:search IS NULL OR :search = '')
                    OR(
                           s.title LIKE CONCAT('%', :search, '%')
                        OR s.content LIKE CONCAT('%', :search, '%')
                        OR s.created_by LIKE CONCAT('%', :search, '%')
                        OR st.keywords LIKE CONCAT('%', :search, '%')
                    )  
                )
            AND ((COALESCE(:startDate, :endDate) IS NULL) OR s.created_at BETWEEN :startDate AND :endDate)
    """,nativeQuery = true)
    int countAllSpeeches(@Param("search") String search,
                         @Param("keywords") List<String> keywords,
                         @Param("startDate") Date startDate,
                         @Param("endDate") Date endDate);
}
