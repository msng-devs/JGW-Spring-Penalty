package com.jaramgroupware.penalty.utils.validation.penalty;

import com.jaramgroupware.penalty.dto.penalty.controllerDto.PenaltyBulkUpdateRequestControllerDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.stream.Collectors;


public class BulkUpdatePenaltyValidator implements ConstraintValidator<BulkUpdatePenaltyValid, Set<PenaltyBulkUpdateRequestControllerDto>> {

    @Override
    public boolean isValid(Set<PenaltyBulkUpdateRequestControllerDto> dtos, ConstraintValidatorContext context) {

        return dtos.stream().map(PenaltyBulkUpdateRequestControllerDto::getId).collect(Collectors.toSet()).size()
                == dtos.size();
    }
}
