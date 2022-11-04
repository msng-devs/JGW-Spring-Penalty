package com.jaramgroupware.penalty.domain.penalty;

import com.jaramgroupware.penalty.TestUtils;
import com.jaramgroupware.penalty.utils.parse.ParseByNameBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SqlGroup({
        @Sql(scripts = "classpath:tableBuild.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "classpath:testDataSet.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@Transactional
@DataJpaTest
class PenaltyRepositoryTest {

    private final TestUtils testUtils = new TestUtils();

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private PenaltyRepository penaltyRepository;
    private final PenaltySpecificationBuilder penaltySpecificationBuilder = new PenaltySpecificationBuilder(new ParseByNameBuilder());
    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findPenaltyById() {
        //given
        Penalty testGoal = testUtils.getTestPenalty();
        //when
        Penalty result = penaltyRepository.findPenaltyById(testGoal.getId())
                .orElseThrow(IllegalArgumentException::new);
        //then
        assertEquals(result.toString(),testGoal.toString());
    }

    @Test
    void findAllBy() {
        //given
        List<Penalty> testGoal = new ArrayList<Penalty>();
        testGoal.add(testUtils.getTestPenalty());
        testGoal.add(testUtils.getTestPenalty2());
        //when
        List<Penalty> results = penaltyRepository.findAllBy()
                .orElseThrow(IllegalArgumentException::new);
        //then
        assertThat(testUtils.isListSame(testGoal,results),is(true));
    }

    @Test
    void save() {
        //given
        Penalty testGoal = testUtils.getTestPenalty();
        testGoal.setId(null);

        //when
        penaltyRepository.save(testGoal);

        //then
        testGoal.setId(3L);
        assertEquals(testGoal.toString(),testEntityManager.find(Penalty.class,testGoal.getId()).toString());
    }

    @Test
    void delete() {
        //given
        Penalty testGoal = testUtils.getTestPenalty();

        //when
        penaltyRepository.delete(testGoal);

        //then
        assertThat(testEntityManager.find(Penalty.class,testGoal.getId()),is(nullValue()));
    }

    @Test
    void findAllByIdIn() {
        //given
        List<Penalty> testGoal = Arrays.asList(testUtils.getTestPenalty(),testUtils.getTestPenalty2());
        Set<Long> testIds = testGoal.stream().map(Penalty::getId).collect(Collectors.toSet());
        //when
        List<Penalty> res =  penaltyRepository.findAllByIdIn(testIds);

        //then
        assertThat(res,is(notNullValue()));
        assertTrue(testUtils.isListSame(res,testGoal));
    }

    @Test
    void deleteAllByIdInQuery() {
        //given
        List<Penalty> testGoal = Arrays.asList(testUtils.getTestPenalty(),testUtils.getTestPenalty2());
        Set<Long> testIds = testGoal.stream().map(Penalty::getId).collect(Collectors.toSet());
        //when
        penaltyRepository.deleteAllByIdInQuery(testIds);

        //then
        assertThat(testEntityManager.find(Penalty.class,testGoal.get(0).getId()),is(nullValue()));
        assertThat(testEntityManager.find(Penalty.class,testGoal.get(1).getId()),is(nullValue()));
    }
    @Test
    void bulkUpdate() {
        //given
        List<Penalty> testGoal = new ArrayList<>();
        testGoal.add(testUtils.getTestPenalty());
        testGoal.add(testUtils.getTestPenalty2());

        //when
        penaltyRepository.bulkUpdate(testGoal,testUtils.getTestUid());
        testEntityManager.flush();
        //then
        assertEquals(testEntityManager.find(Penalty.class,testUtils.getTestPenalty().getId()).getModifiedBy(),testUtils.getTestUid());
        assertEquals(testEntityManager.find(Penalty.class,testUtils.getTestPenalty2().getId()).getModifiedBy(),testUtils.getTestUid());
    }

    @Test
    void findAllWithIntegratedSpec(){
        //given
        Penalty testPenalty = testUtils.getTestPenalty();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("reason",testPenalty.getReason());
        queryParam.add("targetMember",testPenalty.getTargetMember().getId());
        queryParam.add("type", (testPenalty.isType()) ? "true" : "false" );
        queryParam.add("modifiedBy",testPenalty.getModifiedBy());
        queryParam.add("createBy",testPenalty.getCreateBy());
        queryParam.add("startCreatedDateTime",testPenalty.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endCreatedDateTime",testPenalty.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("startModifiedDateTime",testPenalty.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endModifiedDateTime",testPenalty.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testPenalty.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithIntegratedSpec2(){
        //given
        Penalty testPenalty = testUtils.getTestPenalty2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("reason",testPenalty.getReason());
        queryParam.add("targetMember",testPenalty.getTargetMember().getId());
        queryParam.add("type", (testPenalty.isType()) ? "true" : "false" );
        queryParam.add("modifiedBy",testPenalty.getModifiedBy());
        queryParam.add("createBy",testPenalty.getCreateBy());
        queryParam.add("startCreatedDateTime",testPenalty.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endCreatedDateTime",testPenalty.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("startModifiedDateTime",testPenalty.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endModifiedDateTime",testPenalty.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testPenalty.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithReason(){
        //given
        Penalty testPenalty = testUtils.getTestPenalty();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("reason",testPenalty.getReason());
        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testPenalty.toString(),res.getContent().get(0).toString());
    }
    @Test
    void findAllWithReason2(){
        //given
        Penalty testPenalty = testUtils.getTestPenalty2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("reason",testPenalty.getReason());
        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testPenalty.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithTargetMember(){
        //given
        Penalty testPenalty = testUtils.getTestPenalty();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("targetMember",testPenalty.getTargetMember().getId());
        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testPenalty.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithTargetMember2(){
        //given
        Penalty testPenalty = testUtils.getTestPenalty2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("targetMember",testPenalty.getTargetMember().getId());
        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testPenalty.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithType(){
        //given
        Penalty testPenalty = testUtils.getTestPenalty();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("type", (testPenalty.isType()) ? "true" : "false" );
        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testPenalty.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithType2(){
        //given
        Penalty testPenalty = testUtils.getTestPenalty2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("type", (testPenalty.isType()) ? "true" : "false" );
        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testPenalty.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithModifiedBy(){
        //given
        Penalty testPenalty = testUtils.getTestPenalty();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("modifiedBy",testPenalty.getModifiedBy());
        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testPenalty.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithModifiedBy2(){
        //given
        Penalty testPenalty = testUtils.getTestPenalty2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("modifiedBy",testPenalty.getModifiedBy());
        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testPenalty.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithCreateBy(){
        //given
        Penalty testPenalty = testUtils.getTestPenalty();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("createBy",testPenalty.getCreateBy());
        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testPenalty.toString(),res.getContent().get(0).toString());
    }

    @Test
    void findAllWithCreateBy2(){
        //given
        Penalty testPenalty = testUtils.getTestPenalty2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("createBy",testPenalty.getCreateBy());
        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec, PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testPenalty.toString(),res.getContent().get(0).toString());
    }


    @DisplayName("Penalty createdDateTime 검색 테스트 - 시작 범위와 종료 범위가 주어졌을 때")
    @Test
    void findAllWithCreatedDateTimeWithStartAndEnd(){
        //given
        Penalty testPenaltys = testUtils.getTestPenalty();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startCreatedDateTime",testPenaltys.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endCreatedDateTime",testPenaltys.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testPenaltys.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Penalty createdDateTime 검색 테스트 - 시작 범위와 종료 범위가 주어졌을 때2")
    @Test
    void findAllWithCreatedDateTimeWithStartAndEnd2(){
        //given
        Penalty testPenaltys = testUtils.getTestPenalty2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startCreatedDateTime",testPenaltys.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endCreatedDateTime",testPenaltys.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testPenaltys.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Penalty createdDateTime 검색 테스트 - 시작 범위만 주어졌을 때")
    @Test
    void findAllWithCreatedDateTimeWithStart(){
        //given
        Penalty testPenaltys = testUtils.getTestPenalty();
        Penalty testPenaltys2 = testUtils.getTestPenalty2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startCreatedDateTime",testPenaltys.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testPenaltys,testPenaltys2)));
    }

    @DisplayName("Penalty createdDateTime 검색 테스트 - 시작 범위만 주어졌을 때2")
    @Test
    void findAllWithCreatedDateTimeWithStart2(){
        //given
        Penalty testPenaltys = testUtils.getTestPenalty2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startCreatedDateTime",testPenaltys.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testPenaltys.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Penalty createdDateTime 검색 테스트 - 종료 범위만 주어졌을 때")
    @Test
    void findAllWithCreatedDateTimeWithEnd(){
        //given
        Penalty testPenaltys = testUtils.getTestPenalty();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("endCreatedDateTime",testPenaltys.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testPenaltys.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Penalty createdDateTime 검색 테스트 - 종료 범위만 주어졌을 때2")
    @Test
    void findAllWithCreatedDateTimeWithEnd2(){
        //given
        Penalty testPenaltys = testUtils.getTestPenalty2();
        Penalty testPenaltys2 = testUtils.getTestPenalty();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("endCreatedDateTime",testPenaltys.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testPenaltys,testPenaltys2)));
    }

    @DisplayName("Penalty modifiedDateTime 검색 테스트 - 시작 범위와 종료 범위가 주어졌을 때")
    @Test
    void findAllWithModifiedDateTimeWithStartAndEnd(){
        //given
        Penalty testPenaltys = testUtils.getTestPenalty();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startModifiedDateTime",testPenaltys.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endModifiedDateTime",testPenaltys.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testPenaltys.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Penalty modifiedDateTime 검색 테스트 - 시작 범위와 종료 범위가 주어졌을 때2")
    @Test
    void findAllWithModifiedDateTimeWithStartAndEnd2(){
        //given
        Penalty testPenaltys = testUtils.getTestPenalty2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startModifiedDateTime",testPenaltys.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        queryParam.add("endModifiedDateTime",testPenaltys.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testPenaltys.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Penalty modifiedDateTime 검색 테스트 - 시작 범위만 주어졌을 때")
    @Test
    void findAllWithModifiedDateTimeWithStart(){
        //given
        Penalty testPenaltys = testUtils.getTestPenalty();
        Penalty testPenaltys2 = testUtils.getTestPenalty2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startModifiedDateTime",testPenaltys.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testPenaltys,testPenaltys2)));
    }

    @DisplayName("Penalty modifiedDateTime 검색 테스트 - 시작 범위만 주어졌을 때2")
    @Test
    void findAllWithModifiedDateTimeWithStart2(){
        //given
        Penalty testPenaltys = testUtils.getTestPenalty2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("startModifiedDateTime",testPenaltys.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testPenaltys.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Penalty modifiedDateTime 검색 테스트 - 종료 범위만 주어졌을 때")
    @Test
    void findAllWithModifiedDateTimeWithEnd(){
        //given
        Penalty testPenaltys = testUtils.getTestPenalty();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("endModifiedDateTime",testPenaltys.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(1L,res.getTotalElements());
        assertEquals(testPenaltys.toString(),res.getContent().get(0).toString());
    }

    @DisplayName("Penalty modifiedDateTime 검색 테스트 - 종료 범위만 주어졌을 때2")
    @Test
    void findAllWithModifiedDateTimeWithEnd2(){
        //given
        Penalty testPenaltys = testUtils.getTestPenalty2();
        Penalty testPenaltys2 = testUtils.getTestPenalty();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("endModifiedDateTime",testPenaltys.getModifiedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testPenaltys,testPenaltys2)));
    }

    @Test
    void findAll(){
        //given
        Penalty testPenaltys = testUtils.getTestPenalty();
        Penalty testPenaltys2 = testUtils.getTestPenalty2();

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("this is not working query param","this is mock value");

        PenaltySpecification testSpec = penaltySpecificationBuilder.toSpec(queryParam);

        //when
        Page<Penalty> res = penaltyRepository.findAll(testSpec,PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC,"id")));

        //then
        assertThat(res,is(notNullValue()));
        assertEquals(2L,res.getTotalElements());
        assertTrue(testUtils.isListSame(res.getContent(),Arrays.asList(testPenaltys,testPenaltys2)));
    }
}