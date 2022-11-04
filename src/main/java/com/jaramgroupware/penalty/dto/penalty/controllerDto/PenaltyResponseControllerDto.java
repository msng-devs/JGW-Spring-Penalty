package com.jaramgroupware.penalty.dto.penalty.controllerDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PenaltyResponseControllerDto {

    private Long id;
    private String targetMemberID;
    private String targetMemberName;
    private String modifiedBy;
    private boolean type;
    private String reason;
    private LocalDateTime modifiedDateTime;
    private String createdBy;
    private LocalDateTime createdDateTime;

}
