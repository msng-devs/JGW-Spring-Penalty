package com.jaramgroupware.penalty.dto.penalty.serviceDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.penalty.domain.penalty.Penalty;
import com.jaramgroupware.penalty.dto.penalty.controllerDto.PenaltyResponseControllerDto;
import lombok.*;

import java.time.LocalDateTime;


@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PenaltyResponseServiceDto {

    private Long id;
    private String targetMemberID;
    private String targetMemberName;
    private String modifiedBy;
    private boolean type;
    private String reason;
    private LocalDateTime modifiedDateTime;
    private String createdBy;
    private LocalDateTime createdDateTime;

    public PenaltyResponseServiceDto(Penalty penalty){
        id = penalty.getId();
        targetMemberID = penalty.getTargetMember().getId();
        targetMemberName = penalty.getTargetMember().getName();
        type = penalty.isType();
        reason = penalty.getReason();
        createdBy = penalty.getCreateBy();
        createdDateTime = penalty.getCreatedDateTime();
        modifiedBy = penalty.getModifiedBy();
        modifiedDateTime = penalty.getModifiedDateTime();
    }

    public PenaltyResponseControllerDto toControllerDto(){
        return PenaltyResponseControllerDto
                .builder()
                .id(id)
                .targetMemberID(targetMemberID)
                .targetMemberName(targetMemberName)
                .type(type)
                .reason(reason)
                .modifiedBy(modifiedBy)
                .createdDateTime(createdDateTime)
                .modifiedDateTime(modifiedDateTime)
                .createdBy(createdBy)
                .createdDateTime(createdDateTime)
                .build();
    }

}
