package com.jaramgroupware.penalty.dto.penalty.serviceDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.penalty.domain.penalty.Penalty;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PenaltyBulkUpdateRequestServiceDto {

    private String reason;
    private boolean type;
    private Long id;

    public Penalty toEntity(){
        return Penalty.builder()
                .id(id)
                .type(type)
                .reason(reason)
                .build();
    }
    @Override
    public boolean equals(Object o){
        return this.toString().equals(o.toString());
    }
}
