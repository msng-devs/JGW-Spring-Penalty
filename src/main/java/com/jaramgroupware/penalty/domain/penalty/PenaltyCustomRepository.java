package com.jaramgroupware.penalty.domain.penalty;


import java.util.List;

public interface PenaltyCustomRepository {

    void bulkUpdate(List<Penalty> penalties, String who);
}
