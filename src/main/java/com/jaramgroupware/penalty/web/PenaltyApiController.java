package com.jaramgroupware.penalty.web;


import com.jaramgroupware.penalty.domain.penalty.PenaltySpecification;
import com.jaramgroupware.penalty.domain.penalty.PenaltySpecificationBuilder;
import com.jaramgroupware.penalty.domain.role.Role;
import com.jaramgroupware.penalty.dto.general.controllerDto.MessageDto;
import com.jaramgroupware.penalty.dto.penalty.controllerDto.*;
import com.jaramgroupware.penalty.dto.penalty.serviceDto.PenaltyResponseServiceDto;
import com.jaramgroupware.penalty.service.PenaltyService;
import com.jaramgroupware.penalty.utils.exception.CustomException;
import com.jaramgroupware.penalty.utils.exception.ErrorCode;
import com.jaramgroupware.penalty.utils.validation.PageableValid;
import com.jaramgroupware.penalty.utils.validation.penalty.BulkUpdatePenaltyValid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/v1/penalty")
public class PenaltyApiController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PenaltyService penaltyService;
    private final PenaltySpecificationBuilder penaltySpecificationBuilder;

    private final Role adminRole = Role.builder().id(4).build();


    @PostMapping
    public ResponseEntity<MessageDto> addPenalty(
            @RequestBody List<@Valid PenaltyAddRequestControllerDto> penaltyAddRequestControllerDto,
            @RequestHeader("user_uid") String uid){

        penaltyService.add(
                penaltyAddRequestControllerDto.stream().map(PenaltyAddRequestControllerDto::toServiceDto).collect(Collectors.toList()),uid);

        return ResponseEntity.ok(new MessageDto("총 ("+penaltyAddRequestControllerDto.size()+")개의 Penalty를 성공적으로 추가했습니다!"));
    }
    @GetMapping("{penaltyId}")
    public ResponseEntity<PenaltyResponseControllerDto> getPenaltyById(
            @PathVariable Long penaltyId,
            @RequestHeader("user_uid") String uid,
            @RequestHeader("user_role_id") Integer roleID){

        PenaltyResponseControllerDto result = penaltyService.findById(penaltyId).toControllerDto();
        if(roleID < adminRole.getId() && !uid.equals(result.getTargetMemberID())) throw new CustomException(ErrorCode.FORBIDDEN_ROLE);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<PenaltyResponseControllerDto>> getPenaltyAll(
            @PageableDefault(page = 0,size = 1000,sort = "id",direction = Sort.Direction.DESC)
            @PageableValid(sortKeys =
                    {"id","type","targetMember","reason","createdDateTime","modifiedDateTime","createBy","modifiedBy"}
                    ) Pageable pageable,
            @RequestParam(required = false) MultiValueMap<String, String> queryParam,
            @RequestHeader("user_uid") String uid,
            @RequestHeader("user_role_id") Integer roleID){

        if((!queryParam.containsKey("targetMember") || !Objects.equals(queryParam.getFirst("targetMember"), uid)) && roleID < adminRole.getId()){
            throw new CustomException(ErrorCode.FORBIDDEN_ROLE);
        }

        //limit 확인 및 추가
        int limit = queryParam.containsKey("limit") ? Integer.parseInt(Objects.requireNonNull(queryParam.getFirst("limit"))) : -1;

        PenaltySpecification spec = penaltySpecificationBuilder.toSpec(queryParam);

        List<PenaltyResponseControllerDto> results;

        if(limit > 0){
            results = penaltyService.findAll(spec, PageRequest.of(0, limit, pageable.getSort()))
                    .stream()
                    .map(PenaltyResponseServiceDto::toControllerDto)
                    .collect(Collectors.toList());
        }

        else{
            results = penaltyService.findAll(spec,pageable)
                    .stream()
                    .map(PenaltyResponseServiceDto::toControllerDto)
                    .collect(Collectors.toList());
        }


        return ResponseEntity.ok(results);
    }


    @DeleteMapping("{penaltyId}")
    public ResponseEntity<PenaltyIdResponseControllerDto> delPenalty(
            @PathVariable Long penaltyId,
            @RequestHeader("user_uid") String uid){

        penaltyService.delete(penaltyId);

        return ResponseEntity.ok(new PenaltyIdResponseControllerDto(penaltyId));
    }

    @DeleteMapping
    public ResponseEntity<MessageDto> bulkDelPenalty(
            @RequestBody @Valid PenaltyBulkDeleteRequestControllerDto dto,
            @RequestHeader("user_uid") String uid){

        penaltyService.delete(dto.getPenaltyIDs());

        return ResponseEntity.ok(new MessageDto("총 ("+dto.getPenaltyIDs().size()+")개의 Penalty를 성공적으로 추가했습니다!"));
    }

    @PutMapping("{penaltyId}")
    public ResponseEntity<PenaltyResponseControllerDto> updatePenalty(
            @PathVariable Long penaltyId,
            @RequestBody @Valid PenaltyUpdateRequestControllerDto penaltyUpdateRequestControllerDto,
            @RequestHeader("user_uid") String uid){

        PenaltyResponseControllerDto result = penaltyService.update(penaltyId,penaltyUpdateRequestControllerDto.toServiceDto(),uid).toControllerDto();

        return ResponseEntity.ok(result);
    }

    @PutMapping
    public ResponseEntity<MessageDto> bulkUpdatePenalty(
            @RequestBody @NotNull @BulkUpdatePenaltyValid Set<@Valid PenaltyBulkUpdateRequestControllerDto> dto,
            @RequestHeader("user_uid") String uid){

        penaltyService.update(dto.stream()
                        .map(PenaltyBulkUpdateRequestControllerDto::toServiceDto)
                        .collect(Collectors.toList()),uid);

        return ResponseEntity.ok(new MessageDto("총 ("+dto.size()+")개의 Penalty를 성공적으로 업데이트 했습니다!"));
    }
}
