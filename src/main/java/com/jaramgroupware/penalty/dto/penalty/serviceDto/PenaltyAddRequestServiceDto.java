package com.jaramgroupware.penalty.dto.penalty.serviceDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.penalty.domain.member.Member;
import com.jaramgroupware.penalty.domain.penalty.Penalty;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PenaltyAddRequestServiceDto {

    private String reason;
    private Member targetMember;
    private boolean type;

    public Penalty toEntity(){
        return Penalty.builder()
                .reason(reason)
                .targetMember(targetMember)
                .type(type)
                .build();
    }
    @Override
    public boolean equals(Object o){
        return this.toString().equals(o.toString());
    }
}
