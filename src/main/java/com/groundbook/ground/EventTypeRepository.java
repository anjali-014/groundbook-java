package com.groundbook.ground;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/** The event_types table: the source of every buffer. */
@Repository
public class EventTypeRepository {

      /** One event_types row. */
    public record Row(String code, String name, int setupMinutes, int teardownMinutes) {
    }

        private final JdbcClient jdbc;

    public EventTypeRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<Row> all(long venueId) {
              return jdbc.sql("""
                        SELECT code, name, setup_minutes, teardown_minutes FROM event_types
                        WHERE venue_id = ? ORDER BY id""")
                .param(venueId).query(Row.class).list();
    }

        public Optional<Row> one(long venueId, String code) {
        return jdbc.sql("""
                                                 SELECT code, name, setup_minutes, teardown_minutes FROM event_types
                        WHERE venue_id = ? AND code = ?""")
                .params(venueId, code).query(Row.class).optional();
    }

        public void insert(long venueId, String code, String name, int setupMinutes, int teardownMinutes) {
        jdbc.sql("""
                        INSERT INTO event_types (venue_id, code, name, setup_minutes, teardown_minutes)
                        VALUES (?, ?, ?, ?, ?)""")
                .params(venueId, code, name, setupMinutes, teardownMinutes).update();
                    }
}