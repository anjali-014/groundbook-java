package com.groundbook.ground;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/** The grounds table. */
@Repository
public class GroundRepository {

      /** One grounds row. */
    public record Row(long id, String code, String name, LocalTime opens, LocalTime closes, LocalTime sundayOpens, 
    LocalTime sundayCloses) {
    }

    private static final String SELECT = """
            SELECT id, code, name, opens, closes, sunday_opens, sunday_closes FROM grounds WHERE venue_id = ?""";

    private final JdbcClient jdbc;

        public GroundRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<Row> all(long venueId) {
        return jdbc.sql(SELECT + " ORDER BY code").param(venueId).query(Row.class).list();
    }

        public Optional<Row> one(long venueId, String code) {
        return jdbc.sql(SELECT + " AND code = ?").params(venueId, code).query(Row.class).optional();
    }

    public void insert(long venueId, String code, String name, LocalTime opens, LocalTime closes,
                       LocalTime sundayOpens, LocalTime sundayCloses) {
                                jdbc.sql("""
                        INSERT INTO grounds (venue_id, code, name, opens, closes, sunday_opens, sunday_closes)
                        VALUES (?, ?, ?, ?, ?, ?, ?)""")
                .params(venueId, code, name, opens, closes, sundayOpens, sundayCloses).update();
                    }
}