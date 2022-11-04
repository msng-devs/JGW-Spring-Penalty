package com.jaramgroupware.penalty.dto.penalty.controllerDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Set;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PenaltyBulkDeleteRequestControllerDto {

    @NotNull(message = "Penalty_ids가 비워져있습니다!")
    private Set<Long> PenaltyIDs;

}
