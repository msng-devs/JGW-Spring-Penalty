package com.jaramgroupware.penalty.domain.penalty;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PenaltyRepository extends JpaRepository<Penalty,Long>, JpaSpecificationExecutor<Penalty>,PenaltyCustomRepository {
    Optional<Penalty> findPenaltyById(Long id);
    Optional<List<Penalty>> findAllBy();

    @Query("SELECT p FROM PENALTY p JOIN fetch p.targetMember WHERE p.id IN :ids")
    List<Penalty> findAllByIdIn(@Param("ids") Set<Long> ids);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM PENALTY p WHERE p.id IN :ids")
    int deleteAllByIdInQuery(@Param("ids") Set<Long> ids);
}
