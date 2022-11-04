package com.jaramgroupware.penalty.service;

import com.jaramgroupware.penalty.TestUtils;
import com.jaramgroupware.penalty.domain.penalty.Penalty;
import com.jaramgroupware.penalty.domain.penalty.PenaltyRepository;
import com.jaramgroupware.penalty.domain.penalty.PenaltySpecification;
import com.jaramgroupware.penalty.dto.penalty.serviceDto.PenaltyAddRequestServiceDto;
import com.jaramgroupware.penalty.dto.penalty.serviceDto.PenaltyBulkUpdateRequestServiceDto;
import com.jaramgroupware.penalty.dto.penalty.serviceDto.PenaltyResponseServiceDto;
import com.jaramgroupware.penalty.dto.penalty.serviceDto.PenaltyUpdateRequestServiceDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class PenaltyServiceTest {

    @InjectMocks
    private PenaltyService penaltyService;

    @Mock
    private PenaltyRepository penaltyRepository;

    private final TestUtils testUtils = new TestUtils();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void add() {
        //given
        PenaltyAddRequestServiceDto testServiceDto = PenaltyAddRequestServiceDto
                .builder()
                .reason("test")
                .type(true)
                .targetMember(testUtils.getTestMember())
                .build();

        Penalty testEntity = testServiceDto.toEntity();
        testEntity.setId(1L);

        doReturn(testEntity).when(penaltyRepository).save(any());

        //when
        Long resultID = penaltyService.add(testServiceDto,"system");

        //then
        Assertions.assertNotNull(resultID);
        Assertions.assertEquals(resultID, Objects.requireNonNull(resultID));
        verify(penaltyRepository).save(any());
    }

    @Test
    void addAll() {
        //given
        PenaltyAddRequestServiceDto testServiceDto = PenaltyAddRequestServiceDto
                .builder()
                .reason("test")
                .type(true)
                .targetMember(testUtils.getTestMember())
                .build();

        PenaltyAddRequestServiceDto testServiceDto2 = PenaltyAddRequestServiceDto
                .builder()
                .reason("test")
                .type(true)
                .targetMember(testUtils.getTestMember2())
                .build();

        Penalty testEntity = testServiceDto.toEntity();
        testEntity.setId(1L);

        Penalty testEntity2 = testServiceDto2.toEntity();
        testEntity2.setId(1L);

        //when
        penaltyService.add(Arrays.asList(testServiceDto,testServiceDto2),"system");

        //then
        verify(penaltyRepository).saveAll(anyList());
    }

    @Test
    void findById() {

        //given
        Long testID = 1L;
        Penalty testEntity = testUtils.getTestPenalty();

        doReturn(Optional.of(testEntity)).when(penaltyRepository).findById(testID);

        //when
        PenaltyResponseServiceDto result = penaltyService.findById(testID);

        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.toString(), Objects.requireNonNull(result).toString());
        verify(penaltyRepository).findById(testID);
    }

    @Test
    void findAll() {
        //given
        List<Penalty> testList = new ArrayList<Penalty>();

        Penalty testEntity1 = testUtils.getTestPenalty();
        testList.add(testEntity1);

        Penalty testEntity2 = testUtils.getTestPenalty2();
        testList.add(testEntity2);

        doReturn(Optional.of(testList)).when(penaltyRepository).findAllBy();

        //when
        List<PenaltyResponseServiceDto> results = penaltyService.findAll();

        //then
        Assertions.assertNotNull(results);
        Assertions.assertEquals(testList.stream().map(PenaltyResponseServiceDto::new).collect(Collectors.toList()).toString(), Objects.requireNonNull(results).toString());
        verify(penaltyRepository).findAllBy();
    }

    @Test
    void findAllWithSpecAndPage() {
        //given
        List<Penalty> testList = new ArrayList<Penalty>();

        Penalty testEntity1 = testUtils.getTestPenalty();
        testList.add(testEntity1);

        Penalty testEntity2 = testUtils.getTestPenalty2();
        testList.add(testEntity2);

        Specification<Penalty> testSpec = Mockito.mock(PenaltySpecification.class);
        Pageable testPage = Mockito.mock(Pageable.class);
        Page<Penalty> res = new PageImpl<>(testList);

        doReturn(res).when(penaltyRepository).findAll(testSpec,testPage);

        //when
        List<PenaltyResponseServiceDto> results = penaltyService.findAll(testSpec,testPage);

        //then
        Assertions.assertNotNull(results);
        Assertions.assertEquals(testList.stream().map(PenaltyResponseServiceDto::new).collect(Collectors.toList()).toString(), Objects.requireNonNull(results).toString());
        verify(penaltyRepository).findAll(testSpec,testPage);
    }

    @Test
    void delete() {

        //given
        Long testID = 1L;
        Penalty testEntity = testUtils.getTestPenalty();
        doReturn(Optional.of(testEntity)).when(penaltyRepository).findById(testID);

        //when
        Long resultID = penaltyService.delete(testID);

        //then
        Assertions.assertNotNull(resultID);
        Assertions.assertEquals(testID, Objects.requireNonNull(resultID));
        verify(penaltyRepository).findById(testID);
        verify(penaltyRepository).delete(any());
    }

    @Test
    void deleteAll() {

        //given
        Set<Long> ids = new HashSet<>();
        ids.add(testUtils.getTestPenalty().getId());
        ids.add(testUtils.getTestPenalty2().getId());

        doReturn(Arrays.asList(testUtils.getTestPenalty(),testUtils.getTestPenalty2())).when(penaltyRepository).findAllByIdIn(any());
        //when
        penaltyService.delete(ids);

        //then
        verify(penaltyRepository).findAllByIdIn(any());
        verify(penaltyRepository).deleteAllByIdInQuery(any());
    }

    @Test
    void update() {
        //given
        Long testID = 1L;
        PenaltyUpdateRequestServiceDto testDto = PenaltyUpdateRequestServiceDto.builder()
                .reason("test")
                .type(true)
                .build();

        Penalty targetEntity = testUtils.getTestPenalty();

        doReturn(Optional.of(targetEntity)).when(penaltyRepository).findById(testID);

        //when
        PenaltyResponseServiceDto result = penaltyService.update(testID,testDto, testUtils.getTestUid());

        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(testDto.getReason(),Objects.requireNonNull(result).getReason());
        Assertions.assertEquals(testDto.isType(),Objects.requireNonNull(result).isType());
        verify(penaltyRepository).findById(testID);
        verify(penaltyRepository).save(any());

    }

    @Test
    void updateAll() {
        //given
        PenaltyBulkUpdateRequestServiceDto testDto = PenaltyBulkUpdateRequestServiceDto.builder()
                .id(1L)
                .reason("test")
                .type(true)
                .build();

        PenaltyBulkUpdateRequestServiceDto testDto2 = PenaltyBulkUpdateRequestServiceDto.builder()
                .id(2L)
                .reason("test2")
                .type(true)
                .build();

        doReturn(Arrays.asList(testUtils.getTestPenalty(),testUtils.getTestPenalty2())).when(penaltyRepository).findAllByIdIn(any());

        //when
        penaltyService.update(Arrays.asList(testDto,testDto2), testUtils.getTestUid());

        //then
        verify(penaltyRepository).findAllByIdIn(any());

    }
}