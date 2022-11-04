//package com.jaramgroupware.mms.dto.penalty.controllerDto;
//
//import com.fasterxml.jackson.databind.PropertyNamingStrategy;
//import com.fasterxml.jackson.databind.annotation.JsonNaming;
//import com.jaramgroupware.mms.domain.member.Member;
//import com.jaramgroupware.mms.dto.penalty.serviceDto.PenaltyAddRequestServiceDto;
//import lombok.*;
//
//import javax.validation.constraints.NotEmpty;
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Size;
//
//@ToString
//@Getter
//@AllArgsConstructor
//@Builder
//@Data
//@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
//public class IndexPenaltyAddRequestControllerDto {
//
//
//
//    @Size(max = 255,message = "입력가능한 최대 글자수는 255자입니다.")
//    @NotEmpty(message = "사유는 공백으로 할 수 없습니다!")
//    private String reason;
//
//    @NotEmpty(message = "UID가 비여있습니다!")
//    @Size(max = 28,min=28,message = "UID는 28자리여야 합니다.")
//    private String targetMemberUid;
//
//    @NotNull(message = "패널티의 종류가 누락되어있습니다!")
//    private boolean type;
//
//
//    public PenaltyAddRequestServiceDto toServiceDto(Member member){
//        return PenaltyAddRequestServiceDto.builder()
//                .targetMember(member)
//                .type(type)
//                .reason(reason)
//                .build();
//    }
//}
