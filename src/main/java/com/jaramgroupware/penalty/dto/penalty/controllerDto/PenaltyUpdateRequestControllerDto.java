package com.jaramgroupware.penalty.dto.penalty.controllerDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.penalty.dto.penalty.serviceDto.PenaltyUpdateRequestServiceDto;
import lombok.*;

import javax.validation.constraints.*;

@ToString
@Getter
@AllArgsConstructor
@Builder
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PenaltyUpdateRequestControllerDto {

    @Size(max = 255,message = "입력가능한 최대 글자수는 255자입니다.")
    @NotEmpty(message = "사유는 공백으로 할 수 없습니다!")
    private String reason;

    @NotNull(message = "패널티의 종류가 누락되어있습니다!")
    private boolean type;


    public PenaltyUpdateRequestServiceDto toServiceDto(){
        return PenaltyUpdateRequestServiceDto.builder()
                .type(type)
                .reason(reason)
                .build();
    }
}
