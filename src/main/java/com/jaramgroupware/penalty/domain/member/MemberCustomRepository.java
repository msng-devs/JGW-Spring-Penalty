package com.jaramgroupware.penalty.domain.member;



import java.util.List;

public interface MemberCustomRepository {
    void bulkInsert(List<Member> members,String who);
    void bulkUpdate(List<Member> members,String who);
}
