package com.speech.vault.repository;

import com.speech.vault.entity.User;
import com.speech.vault.type.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    @Query(value = """
        SELECT
            COUNT(1)
        FROM
            `user` AS u
        WHERE
            :#{#userType == null || #userType.isEmpty()} =TRUE OR u.user_type IN (:userType)
    """, nativeQuery = true)
    Integer countAllUser(@Param("userType") List<UserType> userType);

    @Query(value = """
        SELECT
            u.username      AS username,
            u.name          AS name,
            u.user_type     AS userType,
            u.created_at    AS createdAt 
        FROM
            `user` AS u
        WHERE
            :#{#userType == null || #userType.isEmpty()} =TRUE OR u.user_type IN (:userType)
        LIMIT
            :nOffset , :nLimit
    """, nativeQuery = true)
    List<Map<String, Object>> getAllUser(@Param("userType") List<UserType> userType, int nOffset, int nLimit);
}
