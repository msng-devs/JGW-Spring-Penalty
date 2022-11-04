package com.jaramgroupware.penalty.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jaramgroupware.penalty.TestUtils;
import com.jaramgroupware.penalty.dto.penalty.controllerDto.*;
import com.jaramgroupware.penalty.dto.penalty.serviceDto.PenaltyResponseServiceDto;
import com.jaramgroupware.penalty.service.MemberService;
import com.jaramgroupware.penalty.service.PenaltyService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;

import static com.jaramgroupware.penalty.RestDocsConfig.field;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
class PenaltyApiControllerTest {

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private PenaltyService penaltyService;

    @MockBean
    private MemberService memberService;

    private final TestUtils testUtils = new TestUtils();

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addPenalty() throws Exception {
        //given
        PenaltyAddRequestControllerDto testPenaltyDto = PenaltyAddRequestControllerDto.builder()
                .targetMemberUid(testUtils.getTestMember().getId())
                .reason("test")
                .type(true)
                .build();

        PenaltyAddRequestControllerDto testPenaltyDto2 = PenaltyAddRequestControllerDto.builder()
                .targetMemberUid(testUtils.getTestMember().getId())
                .reason("test")
                .type(true)
                .build();

        List<PenaltyAddRequestControllerDto> dto = Arrays.asList(testPenaltyDto,testPenaltyDto2);

        //when
        ResultActions result = mvc.perform(
                post("/api/v1/penalty")
                        .header("user_uid",testUtils.getTestUid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("penalty-add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("[].target_member_uid").description("Penalty의 대상 Member(Object)의 UID(Firebase uid)").attributes(field("constraints", "28자 firebase uid")),
                                fieldWithPath("[].reason").description("Penalty의 사유").attributes(field("constraints", "최대 255자")),
                                fieldWithPath("[].type").description("Penalty의 종류").attributes(field("constraints", "True : 경고, False : 주의 "))
                        ),
                        responseFields(
                                fieldWithPath("message").description("새롭게 추가된 penalty의 갯수 ")
                        )
                ));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("총 (2)개의 Penalty를 성공적으로 추가했습니다!"));
        verify(penaltyService).add(anyList(),anyString());
    }

    @Test
    void getPenaltyByIdWithAdmin() throws Exception {
        //given
        Long penaltyID = 1L;

        PenaltyResponseServiceDto penaltyResponseServiceDto = new PenaltyResponseServiceDto(testUtils.getTestPenalty());

        doReturn(penaltyResponseServiceDto).when(penaltyService).findById(penaltyID);


        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/penalty/{penaltyID}",penaltyID)
                        .header("user_uid",testUtils.getTestUid())
                        .header("user_role_id",4)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("penalty-get-single",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("penaltyID").description("대상 Penalty의 id")
                        ),
                        responseFields(
                                fieldWithPath("id").description("대상 penalty의 ID"),
                                fieldWithPath("created_by").description("대상 penalty를 처음 부여한 자"),
                                fieldWithPath("modified_by").description("대상 penalty를 마지막으로 수정한 자"),
                                fieldWithPath("created_date_time").description("대상 penalty가 생성된 일자"),
                                fieldWithPath("modified_date_time").description("대상 penalty가 마지막으로 수정된 일자"),
                                fieldWithPath("type").description("대상 penalty의 종류"),
                                fieldWithPath("reason").description("대상 penalty의 종류"),
                                fieldWithPath("target_member_id").description("대상 penalty가 부여된 Member(Object) ID"),
                                fieldWithPath("target_member_name").description("대상 penalty가 부여된 Member(Object) Name")
                        )
                ));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(penaltyResponseServiceDto.toControllerDto())));
        verify(penaltyService).findById(penaltyID);
    }


    @Test
    void getPenaltyByIdWithNoAdminSelf() throws Exception {
        //given
        Long penaltyID = 1L;

        PenaltyResponseServiceDto penaltyResponseServiceDto = new PenaltyResponseServiceDto(testUtils.getTestPenalty());

        doReturn(penaltyResponseServiceDto).when(penaltyService).findById(penaltyID);


        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/penalty/{penaltyID}",penaltyID)
                        .header("user_uid",testUtils.getTestUid())
                        .header("user_role_id",3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(penaltyResponseServiceDto.toControllerDto())));
        verify(penaltyService).findById(penaltyID);
    }

    @Test
    void getPenaltyByIdWithNoAdminAuthError() throws Exception {
        //given
        Long penaltyID = 1L;

        PenaltyResponseServiceDto penaltyResponseServiceDto = new PenaltyResponseServiceDto(testUtils.getTestPenalty());

        doReturn(penaltyResponseServiceDto).when(penaltyService).findById(penaltyID);


        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/penalty/{penaltyID}",penaltyID)
                        .header("user_uid",testUtils.getTestMember2().getId())
                        .header("user_role_id",3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isForbidden());
        verify(penaltyService).findById(penaltyID);
    }

    @Test
    void getPenaltyAllWithAdminNoSelf() throws Exception {
        //given
        List<PenaltyResponseServiceDto> targetPenaltyList = new ArrayList<PenaltyResponseServiceDto>();

        PenaltyResponseServiceDto penaltyResponseServiceDto1 = new PenaltyResponseServiceDto(testUtils.getTestPenalty());
        targetPenaltyList.add(penaltyResponseServiceDto1);

        PenaltyResponseServiceDto penaltyResponseServiceDto2 = new PenaltyResponseServiceDto(testUtils.getTestPenalty2());
        targetPenaltyList.add(penaltyResponseServiceDto2);

        doReturn(targetPenaltyList).when(penaltyService).findAll(any(),any());

        //when
        ResultActions result = mvc.perform(
                get("/api/v1/penalty/")
                        .header("user_uid",testUtils.getTestUid())
                        .header("user_role_id",4)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("penalty-get-multiple",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("대상 penalty의 ID"),
                                fieldWithPath("[].created_by").description("대상 penalty를 처음 부여한 자"),
                                fieldWithPath("[].modified_by").description("대상 penalty를 마지막으로 수정한 자"),
                                fieldWithPath("[].created_date_time").description("대상 penalty가 생성된 일자"),
                                fieldWithPath("[].modified_date_time").description("대상 penalty가 마지막으로 수정된 일자"),
                                fieldWithPath("[].type").description("대상 penalty의 종류"),
                                fieldWithPath("[].reason").description("대상 penalty의 종류"),
                                fieldWithPath("[].target_member_id").description("대상 penalty가 부여된 Member(Object)의 ID"),
                                fieldWithPath("[].target_member_name").description("대상 penalty가 부여된 Member(Object)의 Name")
                        )
                ));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                targetPenaltyList.stream()
                                        .map(PenaltyResponseServiceDto::toControllerDto)
                                        .collect(Collectors.toList()))));
        verify(penaltyService).findAll(any(),any());
    }
    @Test
    void getPenaltyAllWithAdminSelf() throws Exception {
        //given
        List<PenaltyResponseServiceDto> targetPenaltyList = new ArrayList<PenaltyResponseServiceDto>();

        PenaltyResponseServiceDto penaltyResponseServiceDto1 = new PenaltyResponseServiceDto(testUtils.getTestPenalty());
        targetPenaltyList.add(penaltyResponseServiceDto1);

        doReturn(targetPenaltyList).when(penaltyService).findAll(any(),any());
        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();

        queryParam.add("targetMember",penaltyResponseServiceDto1.getTargetMemberID());
        //when
        ResultActions result = mvc.perform(
                get("/api/v1/penalty/")
                        .header("user_uid",testUtils.getTestUid())
                        .header("user_role_id",4)
                        .queryParams(queryParam)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                targetPenaltyList.stream()
                                        .map(PenaltyResponseServiceDto::toControllerDto)
                                        .collect(Collectors.toList()))));
        verify(penaltyService).findAll(any(),any());
    }

    @Test
    void getPenaltyAllWithNoAdminSelf() throws Exception {
        //given
        List<PenaltyResponseServiceDto> targetPenaltyList = new ArrayList<PenaltyResponseServiceDto>();

        PenaltyResponseServiceDto penaltyResponseServiceDto1 = new PenaltyResponseServiceDto(testUtils.getTestPenalty());
        targetPenaltyList.add(penaltyResponseServiceDto1);


        doReturn(targetPenaltyList).when(penaltyService).findAll(any(),any());
        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();

        queryParam.add("targetMember",penaltyResponseServiceDto1.getTargetMemberID());
        //when
        ResultActions result = mvc.perform(
                get("/api/v1/penalty/")
                        .header("user_uid",testUtils.getTestUid())
                        .header("user_role_id",3)
                        .queryParams(queryParam)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                targetPenaltyList.stream()
                                        .map(PenaltyResponseServiceDto::toControllerDto)
                                        .collect(Collectors.toList()))));
        verify(penaltyService).findAll(any(),any());
    }
    @Test
    void getPenaltyAllWithNoAdminWithAuthError() throws Exception {
        //given
        List<PenaltyResponseServiceDto> targetPenaltyList = new ArrayList<PenaltyResponseServiceDto>();

        PenaltyResponseServiceDto penaltyResponseServiceDto1 = new PenaltyResponseServiceDto(testUtils.getTestPenalty());
        targetPenaltyList.add(penaltyResponseServiceDto1);


        doReturn(targetPenaltyList).when(penaltyService).findAll(any(),any());

        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("targetMember",penaltyResponseServiceDto1.getTargetMemberID());
        //when
        ResultActions result = mvc.perform(
                get("/api/v1/penalty/")
                        .header("user_uid",testUtils.getTestMember2().getId())
                        .header("user_role_id",3)
                        .queryParams(queryParam)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isForbidden());
    }
    @Test
    void delPenalty() throws Exception {
        //given
        Long penaltyID = 1L;

        doReturn(penaltyID).when(penaltyService).delete(penaltyID);

        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.delete("/api/v1/penalty/{penaltyID}",penaltyID)
                        .header("user_uid",testUtils.getTestUid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("penalty-del-single",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("penaltyID").description("삭제할 Penalty의 id")
                        ),
                        responseFields(
                                fieldWithPath("penalty_id").description("삭제된 penalty의 ID")
                        )
                ));
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.penalty_id").value("1"));
        verify(penaltyService).delete(penaltyID);
    }

    @Test
    void bulkDeletePenalty() throws Exception {
        //given
        Set<Long> ids = new HashSet<>();
        ids.add(testUtils.getTestPenalty().getId());
        ids.add(testUtils.getTestPenalty2().getId());

        PenaltyBulkDeleteRequestControllerDto dto = PenaltyBulkDeleteRequestControllerDto.builder()
                .PenaltyIDs(ids)
                .build();
        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.delete("/api/v1/penalty")
                        .header("user_uid",testUtils.getTestUid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andDo(document("penalty-del-bulk",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("penalty_ids").description("삭제할 Penalty의 id")
                        ),
                        responseFields(
                                fieldWithPath("message").description("삭제된 penalty의 ID")
                        )
                ));
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("총 (2)개의 Penalty를 성공적으로 추가했습니다!"));
        verify(penaltyService).delete(anySet());
    }

    @Test
    void updatePenalty() throws Exception {
        //given
        Long penaltyID = 1L;

        PenaltyUpdateRequestControllerDto penaltyUpdateRequestServiceDto = PenaltyUpdateRequestControllerDto.builder()
                .reason("test")
                .type(true)
                .build();

        PenaltyResponseServiceDto penaltyResponseServiceDto = new PenaltyResponseServiceDto(testUtils.getTestPenalty());

        doReturn(penaltyResponseServiceDto).when(penaltyService).update(penaltyID,penaltyUpdateRequestServiceDto.toServiceDto(),testUtils.getTestUid());

        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.put("/api/v1/penalty/{penaltyID}",penaltyID)
                        .header("user_uid",testUtils.getTestUid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(penaltyUpdateRequestServiceDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("penalty-update-single",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("penaltyID").description("대상 Penalty의 id")
                        ),
                        requestFields(
                                fieldWithPath("reason").description("Penalty의 사유").attributes(field("constraints", "최대 255자")),
                                fieldWithPath("type").description("Penalty의 종류").attributes(field("constraints", "True : 경고, False : 주의 "))
                        ),
                        responseFields(
                                fieldWithPath("id").description("대상 penalty의 ID"),
                                fieldWithPath("created_by").description("대상 penalty를 처음 부여한 자"),
                                fieldWithPath("modified_by").description("대상 penalty를 마지막으로 수정한 자"),
                                fieldWithPath("created_date_time").description("대상 penalty가 생성된 일자"),
                                fieldWithPath("modified_date_time").description("대상 penalty가 마지막으로 수정된 일자"),
                                fieldWithPath("type").description("대상 penalty의 종류"),
                                fieldWithPath("reason").description("대상 penalty의 종류"),
                                fieldWithPath("target_member_id").description("대상 penalty가 부여된 Member(Object)의 ID"),
                                fieldWithPath("target_member_name").description("대상 penalty가 부여된 Member(Object)의 이름")
                                )
                ));
        //then
        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(penaltyResponseServiceDto.toControllerDto())));
        verify(penaltyService).update(penaltyID,penaltyUpdateRequestServiceDto.toServiceDto(),testUtils.getTestUid());
    }

    @Test
    void bulkUpdate() throws Exception {
        //given
        PenaltyBulkUpdateRequestControllerDto penaltyUpdateRequestServiceDto = PenaltyBulkUpdateRequestControllerDto.builder()
                .id(testUtils.getTestPenalty().getId())
                .reason("test")
                .type(true)
                .build();

        PenaltyBulkUpdateRequestControllerDto penaltyUpdateRequestServiceDto2 = PenaltyBulkUpdateRequestControllerDto.builder()
                .id(testUtils.getTestPenalty2().getId())
                .reason("test")
                .type(true)
                .build();


        PenaltyResponseServiceDto penaltyResponseServiceDto = new PenaltyResponseServiceDto(testUtils.getTestPenalty());
        Set<PenaltyBulkUpdateRequestControllerDto> testDto = new HashSet<>();
        testDto.add(penaltyUpdateRequestServiceDto);
        testDto.add(penaltyUpdateRequestServiceDto2);

        //when
        ResultActions result = mvc.perform(
                RestDocumentationRequestBuilders.put("/api/v1/penalty")
                        .header("user_uid",testUtils.getTestUid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("penalty-update-bulk",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("[].id").description("업데이트할 대상 peanlty의 ID"),
                                fieldWithPath("[].reason").description("Penalty의 사유").attributes(field("constraints", "최대 255자")),
                                fieldWithPath("[].type").description("Penalty의 종류").attributes(field("constraints", "True : 경고, False : 주의 "))
                        ),
                        responseFields(
                                fieldWithPath("message").description("업데이트된 penalty의 갯수")
                        )));
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("총 (2)개의 Penalty를 성공적으로 업데이트 했습니다!"));
        verify(penaltyService).update(any(),any());
    }
}