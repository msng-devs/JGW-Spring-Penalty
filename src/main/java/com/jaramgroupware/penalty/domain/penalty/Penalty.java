package com.jaramgroupware.penalty.domain.penalty;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.penalty.domain.BaseEntity;
import com.jaramgroupware.penalty.domain.member.Member;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AttributeOverrides({
        @AttributeOverride(name = "createdDateTime",column = @Column(name = "PENALTY_CREATED_DTTM")),
        @AttributeOverride(name = "modifiedDateTime",column = @Column(name = "PENALTY_MODIFIED_DTTM")),
        @AttributeOverride(name = "createBy",column = @Column(name = "PENALTY_CREATED_BY",length = 30)),
        @AttributeOverride(name = "modifiedBy",column = @Column(name = "PENALTY_MODIFIED_BY",length = 30)),
})
@Entity(name = "PENALTY")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Penalty extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="PENALTY_PK")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_MEMBER_PK",nullable = false)
    private Member targetMember;

    @Column(name = "PENALTY_TYPE",nullable = false)
    private boolean type;

    @Column(name = "PENALTY_REASON",nullable = false)
    private String reason;

    public void update(Penalty penalty,String who){
        type = penalty.isType();
        reason = penalty.getReason();
        modifiedBy = who;
    }


}
