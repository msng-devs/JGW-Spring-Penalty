package com.jaramgroupware.penalty.dto.member.serviceDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.penalty.domain.major.Major;
import com.jaramgroupware.penalty.domain.member.Member;
import com.jaramgroupware.penalty.domain.rank.Rank;
import com.jaramgroupware.penalty.domain.role.Role;
import lombok.*;

import java.time.LocalDate;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MemberBulkUpdateRequestServiceDto {

    private String id;
    private String name;
    private String phoneNumber;
    private Major major;
    private Rank rank;
    private Role role;
    private Integer year;
    private String email;
    private String studentID;
    private boolean leaveAbsence;
    private LocalDate dateOfBirth;

    public Member toEntity(){
        return Member.builder()
                .id(id)
                .email(email)
                .name(name)
                .phoneNumber(phoneNumber)
                .studentID(studentID)
                .major(major)
                .rank(rank)
                .role(role)
                .year(year)
                .leaveAbsence(leaveAbsence)
                .dateOfBirth(dateOfBirth)
                .build();
    }
    @Override
    public boolean equals(Object o){
        return this.toString().equals(o.toString());
    }
}
