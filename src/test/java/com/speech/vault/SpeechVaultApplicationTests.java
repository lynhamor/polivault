package com.speech.vault;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.speech.vault.dto.ResponseDto;
import com.speech.vault.dto.speech.SpeechDto;
import com.speech.vault.dto.speech.SpeechesFilterDto;
import com.speech.vault.entity.Speech;
import com.speech.vault.entity.SpeechTag;
import com.speech.vault.entity.User;
import com.speech.vault.mapper.SpeechMapper;
import com.speech.vault.repository.SpeechRepository;
import com.speech.vault.repository.SpeechTagRepository;
import com.speech.vault.repository.UserRepository;
import com.speech.vault.service.SpeechService;
import com.speech.vault.type.SpeechStatusType;
import com.speech.vault.type.StatusType;
import com.speech.vault.type.UserType;
import org.hibernate.mapping.Any;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpeechVaultApplicationTests {


    @Mock
    private UserRepository userRepository;

    @Mock
    private SpeechRepository speechRepository;

    @Mock
    private SpeechTagRepository speechTagRepository;

    @InjectMocks
    private SpeechService speechService;

    @Mock
    private static SpeechMapper speechMapper;

    private static User mockUser;
    private static Map<String, Object> mockMap;
    private static List<Map<String, Object>> mockMapList;
    private static SpeechDto mockDto;
    private static SpeechesFilterDto mockFilterDto;
    private static SpeechesFilterDto mockFilterDtoInvalid;
    private static Speech mockSpeech;
    private static SpeechTag mockSpeechTag;

    @BeforeAll
    static void init() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        mockUser = User.builder()
                .username("johndoe")
                .name("John Doe")
                .userType(UserType.POLITICIAN)
                .createdAt(new Date())
                .build();

        mockMap = Map.of(
                "id", 1L,
                "slug", "speech-1",
                "title", "speech 1",
                "content","some content",
                "status", "PUBLISHED",
                "author", "john doe",
                "keywords", List.of("key1", "key2", "key3")
        );

        mockMapList = List.of(
                Map.of(
                        "id", 1L,
                        "slug", "speech-1",
                        "title", "speech 1",
                        "content","some content",
                        "status", SpeechStatusType.PUBLISHED,
                        "author", "john doe",
                        "keywords", List.of("key1", "key2", "key3")
                ),
                Map.of(
                        "id", 2L,
                        "slug", "speech-2",
                        "title", "speech 2",
                        "content","some content",
                        "status", SpeechStatusType.DRAFT,
                        "author", "john doe2",
                        "keywords", List.of("key1", "key2", "key3")
                ),
                Map.of(
                        "id", 3L,
                        "slug", "speech-3",
                        "title", "speech 3",
                        "content","some content",
                        "status", SpeechStatusType.ARCHIVED,
                        "author", "john doe3",
                        "keywords", List.of("key1", "key2", "key3")
                )
        );

        mockDto = SpeechDto.builder()
                .slug("speech-1")
                .title("speech 1")
                .content("some content")
                .author("john doe")
                .keywords(List.of("key1", "key2", "key3"))
                .eventAt(new Date())
                .status(SpeechStatusType.PUBLISHED)
                .build();

        mockFilterDto = SpeechesFilterDto.builder()
                .page(1)
                .pageSize(20)
                .build();

        mockFilterDtoInvalid = SpeechesFilterDto.builder()
                .status(List.of(SpeechStatusType.DELETED.name()))
                .page(1)
                .pageSize(20)
                .build();

        mockSpeech = Speech.builder()
                .id(1L)
                .slug(mockDto.getSlug())
                .title(mockDto.getTitle())
                .content(mockDto.getContent())
                .eventAt(mockDto.getEventAt())
                .status(SpeechStatusType.PUBLISHED)
                .build();

        mockSpeechTag = SpeechTag.builder()
                .speechId(mockSpeech.getId())
                .keywords(mapper.writeValueAsString(mockDto.getKeywords()))
                .build();
    }
    @Test
    void testGetAllSpeech() {

        when(speechRepository.getAllSpeeches(any(), any(), any(), any(), any(), anyInt(), anyInt())).thenReturn(mockMapList);

        ResponseDto result = speechService.getAllSpeeches(mockFilterDto);

        assertNotNull(result);
        assertEquals(StatusType.SUCCESS, result.getStatusType());

    }
    @Test
    void testGetAllSpeechInvalid() {

        when(speechRepository.countAllSpeeches(any(), any(), any(), any(), any())).thenReturn(null);

        ResponseDto result = speechService.getAllSpeeches(mockFilterDtoInvalid);

        assertNotNull(result);
        assertTrue(result.getStatusType().equals(StatusType.INVALID));

    }

    @Test
    void testValidSharedSpeech(){

        mockDto.setId(1L);
        when(speechRepository.getSharedSpeech(1L, null)).thenReturn(Optional.of(mockMap));
        when(speechMapper.mapToSpeechDto(mockMap)).thenReturn(mockDto);

        SpeechDto result = speechService.getSharedSpeech(1L, null);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("speech-1", result.getSlug());

    }

    @Test
    void testSetValidSpeech() {

        when(userRepository.findByUsername(mockDto.getAuthor())).thenReturn(Optional.of(mockUser));
        when(speechMapper.toSpeech(mockDto)).thenReturn(mockSpeech);
        when(speechRepository.save(mockSpeech)).thenReturn(mockSpeech);
        when(speechTagRepository.save(mockSpeechTag)).thenReturn(mockSpeechTag);

        ResponseDto result = speechService.setSpeech(mockDto);

        assertNotNull(result);
        assertTrue(result.getStatusType().equals(StatusType.SUCCESS));

    }

    @Test
    void testSetInValidSpeech() {

        when(userRepository.findByUsername(mockDto.getAuthor())).thenReturn(Optional.of(mockUser));
        when(speechMapper.toSpeech(mockDto)).thenReturn(mockSpeech);
        when(speechRepository.save(mockSpeech)).thenReturn(null);

        ResponseDto result = speechService.setSpeech(mockDto);

        assertNotNull(result);
        assertFalse(result.getStatusType().equals(StatusType.SUCCESS));

    }

    @Test
    void testInvalidSharedSpeech(){

        when(speechRepository.getSharedSpeech(1L, null)).thenReturn(Optional.empty());

        SpeechDto result = speechService.getSharedSpeech(1L, null);

        assertNull(result);

    }

}
