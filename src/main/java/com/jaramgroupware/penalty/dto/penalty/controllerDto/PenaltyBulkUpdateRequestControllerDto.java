package com.jaramgroupware.penalty.dto.penalty.controllerDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.penalty.dto.penalty.serviceDto.PenaltyBulkUpdateRequestServiceDto;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@ToString
@Getter
@AllArgsConstructor
@Builder
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PenaltyBulkUpdateRequestControllerDto {

    @Positive(message = "id의 형식이 잘못되었습니다!")
    @NotNull(message = "id가 비워져 있습니다!")
    private Long id;

    @Size(max = 255,message = "입력가능한 최대 글자수는 255자입니다.")
    @NotEmpty(message = "사유는 공백으로 할 수 없습니다!")
    private String reason;

    @NotNull(message = "패널티의 종류가 누락되어있습니다!")
    private boolean type;


    public PenaltyBulkUpdateRequestServiceDto toServiceDto(){
        return PenaltyBulkUpdateRequestServiceDto.builder()
                .id(id)
                .type(type)
                .reason(reason)
                .build();
    }

}
