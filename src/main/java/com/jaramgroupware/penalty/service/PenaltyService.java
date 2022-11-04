package com.jaramgroupware.penalty.service;



import com.jaramgroupware.penalty.domain.penalty.Penalty;
import com.jaramgroupware.penalty.domain.penalty.PenaltyRepository;
import com.jaramgroupware.penalty.dto.penalty.serviceDto.PenaltyAddRequestServiceDto;
import com.jaramgroupware.penalty.dto.penalty.serviceDto.PenaltyBulkUpdateRequestServiceDto;
import com.jaramgroupware.penalty.dto.penalty.serviceDto.PenaltyResponseServiceDto;
import com.jaramgroupware.penalty.dto.penalty.serviceDto.PenaltyUpdateRequestServiceDto;
import com.jaramgroupware.penalty.utils.exception.CustomException;
import com.jaramgroupware.penalty.utils.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PenaltyService {

    @Autowired
    private final PenaltyRepository penaltyRepository;

    private final int batchSize = 100;

    @Transactional
    public Long add(PenaltyAddRequestServiceDto penaltyAddRequestServiceDto,String who){
        Penalty targetPenalty = penaltyAddRequestServiceDto.toEntity();
        targetPenalty.setCreateBy(who);
        targetPenalty.setModifiedBy(who);
        return penaltyRepository.save(targetPenalty).getId();
    }

    @Transactional
    public void add(List<PenaltyAddRequestServiceDto> dtos,String who){
        List<PenaltyAddRequestServiceDto> batchDto = new ArrayList<>();
        for (PenaltyAddRequestServiceDto dto:dtos) {
            batchDto.add(dto);
            if(batchDto.size() == batchSize){
                batchAdd(batchDto,who);
            }
        }
        if(!batchDto.isEmpty()) {
            batchAdd(batchDto,who);
        }
    }

    private void batchAdd(List<PenaltyAddRequestServiceDto> batchDto,String who){
        List<Penalty> targetPenalty = batchDto.stream()
                .map(PenaltyAddRequestServiceDto::toEntity)
                .collect(Collectors.toList());

        targetPenalty.forEach(penalty -> penalty.setCreateBy(who));
        targetPenalty.forEach(penalty -> penalty.setModifiedBy(who));

        penaltyRepository.saveAll(targetPenalty);
        batchDto.clear();
        targetPenalty.clear();
    }

    @Transactional
    public Long delete(Long id){

        Penalty targetPenalty =  penaltyRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_PENALTY_ID));

        penaltyRepository.delete(targetPenalty);
        return id;
    }

    @Transactional
    public Set<Long> delete(Set<Long> ids){

        Set<Long> batchDto = new HashSet<>();
        for (Long dto:ids) {
            batchDto.add(dto);
            if(batchDto.size() == batchSize){
                batchDelete(batchDto);
            }
        }
        if(!batchDto.isEmpty()) {
            batchDelete(batchDto);
        }

        return ids;
    }

    private void batchDelete(Set<Long> batchDto){
        if(penaltyRepository.findAllByIdIn(batchDto).size() != batchDto.size())
            throw new IllegalArgumentException("찾을 수 없는 ID가 들어있습니다.");

        penaltyRepository.deleteAllByIdInQuery(batchDto);
        batchDto.clear();
    }

    @Transactional(readOnly = true)
    public PenaltyResponseServiceDto findById(Long id){

        Penalty targetPenalty = penaltyRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_PENALTY_ID));

        return new PenaltyResponseServiceDto(targetPenalty);
    }

    @Transactional(readOnly = true)
    public List<PenaltyResponseServiceDto> findAll(){

        return penaltyRepository.findAllBy()
                .orElseThrow(()-> new CustomException(ErrorCode.EMPTY_PENALTY))
                .stream().map(PenaltyResponseServiceDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PenaltyResponseServiceDto> findAll(Specification<Penalty> specification, Pageable pageable){
        return penaltyRepository.findAll(specification,pageable)
                .stream()
                .map(PenaltyResponseServiceDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public PenaltyResponseServiceDto update(Long id, PenaltyUpdateRequestServiceDto penaltyUpdateRequestServiceDto,String who){

        Penalty targetPenalty = penaltyRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_PENALTY_ID));

        targetPenalty.update(penaltyUpdateRequestServiceDto.toEntity(),who);

        penaltyRepository.save(targetPenalty);

        return new PenaltyResponseServiceDto(targetPenalty);
    }

    @Transactional
    public void update(List<PenaltyBulkUpdateRequestServiceDto> updateDtos, String who){

        List<PenaltyBulkUpdateRequestServiceDto> batchDto = new ArrayList<>();
        for (PenaltyBulkUpdateRequestServiceDto dto:updateDtos) {
            batchDto.add(dto);
            if(batchDto.size() == batchSize){
                batchDto.add(dto);
                if(batchDto.size() == batchSize){
                    batchUpdate(batchDto,who);
                }
            }
        }
        if(!batchDto.isEmpty()) {
            batchUpdate(batchDto,who);
        }

    }

    private void batchUpdate(List<PenaltyBulkUpdateRequestServiceDto> batchDto,String who){
        Set<Long> ids = batchDto.stream().map(PenaltyBulkUpdateRequestServiceDto::getId).collect(Collectors.toSet());
        if(penaltyRepository.findAllByIdIn(ids).size() != ids.size())
            throw new IllegalArgumentException("찾을 수 없는 ID가 들어있습니다.");
        penaltyRepository.bulkUpdate(batchDto.stream()
                .map(PenaltyBulkUpdateRequestServiceDto::toEntity)
                .collect(Collectors.toList()), who);
        batchDto.clear();
        ids.clear();
    }
}
