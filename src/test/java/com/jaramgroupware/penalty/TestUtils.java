package com.jaramgroupware.penalty;

import com.jaramgroupware.penalty.domain.major.Major;
import com.jaramgroupware.penalty.domain.member.Member;
import com.jaramgroupware.penalty.domain.penalty.Penalty;
import com.jaramgroupware.penalty.domain.rank.Rank;
import com.jaramgroupware.penalty.domain.role.Role;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@Component
public class TestUtils {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final LocalDate testDate;
    private final LocalDate testDate2;
    private final Major testMajor;
    private final Major testMajor2;
    private final Role testRole;
    private final Role testRole2;
    private final Rank testRank;
    private final Rank testRank2;
    private final LocalDateTime testDateTime = LocalDateTime.parse("2022-08-04 04:16:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    private final LocalDateTime testDateTime2 = LocalDateTime.parse("2022-08-28 04:16:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    private final Member testMember;
    private final Member testMember2;
    public final Penalty testPenalty;
    public final Penalty testPenalty2;
    public final String testUid;

    public boolean isListSame(List<?> targetListA , List<?> targetListB){

        if(targetListA.size() != targetListB.size()) return false;
        for (int i = 0; i < targetListA.size(); i++) {
            try{
                targetListA.indexOf(targetListB.get(i));
            }catch (Exception e){
                logger.debug("{}",targetListA.get(i).toString());
                logger.debug("{}",targetListB.get(i).toString());
                return false;
            }
        }
        return true;
    }
    public TestUtils(){

        testDate = LocalDate.of(2022,1,22);
        testDate2 = LocalDate.of(2022,8,28);
        testMajor = Major.builder()
                .id(1)
                .name("인공지능학과")
                .build();
        testMajor2 = Major.builder()
                .id(2)
                .name("소프트웨어학부 컴퓨터 전공")
                .build();
        testRole = Role.builder()
                .id(1)
                .name("ROLE_ADMIN")
                .build();
        testRole2 = Role.builder()
                .id(2)
                .name("ROLE_DEV")
                .build();
        testRank = Rank.builder()
                .id(1)
                .name("정회원")
                .build();
        testRank2 = Rank.builder()
                .id(2)
                .name("준OB")
                .build();
        testMember = Member.builder()
                .id("Th1s1sNotRea1U1DDOY0UKNOWH0S")
                .name("황테스트")
                .email("hwangTest@test.com")
                .phoneNumber("01000000000")
                .studentID("2022000004")
                .year(38)
                .role(testRole)
                .rank(testRank)
                .major(testMajor)
                .leaveAbsence(false)
                .dateOfBirth(testDate)
                .build();
        testMember.setModifiedBy("system");
        testMember.setCreateBy("system");
        testMember.setCreatedDateTime(testDateTime);
        testMember.setModifiedDateTime(testDateTime);

        testMember2 = Member.builder()
                .id("ThiS1SNotRea1U1DDOY0UKNOWHoS")
                .name("김테스트")
                .email("kimTest@test.com")
                .phoneNumber("01000000011")
                .studentID("2022000005")
                .year(37)
                .role(testRole2)
                .rank(testRank2)
                .major(testMajor2)
                .leaveAbsence(true)
                .dateOfBirth(testDate2)
                .build();
        testMember2.setModifiedBy("system2");
        testMember2.setCreateBy("system2");
        testMember2.setCreatedDateTime(testDateTime2);
        testMember2.setModifiedDateTime(testDateTime2);

        testPenalty = Penalty.builder()
                .id(1L)
                .targetMember(testMember)
                .type(true)
                .reason("He insulted Hos.")
                .build();

        testPenalty.setModifiedBy("system");
        testPenalty.setCreateBy("system");
        testPenalty.setCreatedDateTime(testDateTime);
        testPenalty.setModifiedDateTime(testDateTime);

        testPenalty2 = Penalty.builder()
                .id(2L)
                .targetMember(testMember2)
                .type(false)
                .reason("he hates hos.")
                .build();

        testPenalty2.setModifiedBy("system2");
        testPenalty2.setCreateBy("system2");
        testPenalty2.setCreatedDateTime(testDateTime2);
        testPenalty2.setModifiedDateTime(testDateTime2);
        testUid = testMember.getId();
    }
    public HttpEntity<?> createHttpEntity(Object dto,String userUid){

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user_uid",userUid);

        return new HttpEntity<>(dto, headers);
    }
    public Map<String,Object> getString(String arg, Object value) {
        return Collections.singletonMap(arg, value);
    }
}
