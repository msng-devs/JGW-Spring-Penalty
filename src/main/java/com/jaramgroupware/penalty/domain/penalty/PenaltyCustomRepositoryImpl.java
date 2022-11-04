package com.jaramgroupware.penalty.domain.penalty;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class PenaltyCustomRepositoryImpl implements PenaltyCustomRepository {

    private final JdbcTemplate jdbcTemplate;

    public void bulkUpdate(List<Penalty> penalties, String who) {
        String sql = "UPDATE `PENALTY`"
                + " SET `PENALTY_TYPE` = (?), `PENALTY_REASON` = (?), `PENALTY_MODIFIED_BY` = (?), `PENALTY_MODIFIED_DTTM` = (?)"
                + " WHERE `PENALTY_PK` = (?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Penalty penalty = penalties.get(i);
                ps.setBoolean(1,penalty.isType());
                ps.setString(2,penalty.getReason());
                ps.setString(3,who);
                ps.setObject(4,LocalDateTime.now());
                ps.setLong(5, penalty.getId());
            }

            @Override
            public int getBatchSize() {
                return penalties.size();
            }
        });
        penalties.clear();
    }
}
