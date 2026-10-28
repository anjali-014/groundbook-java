package com.groundbook.booking;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/** The bookings table. The club's name is kept here and never sent back. */
@Repository
public class BookingRepository {

    /** One booking, with its ground's code and its event type's code and buffers (its shadow comes from them). */
    public record Row(long id, String ground, String event, Instant from, Instant to, String state, int setupMinutes,
                      int teardownMinutes) {
     
        public String bookingId() {
            return BookingIds.format(id);
        }
    }

    private static final String SELECT = """
            SELECT b.id, g.code AS ground, t.code AS event, b.from_at AS "from", b.to_at AS "to", b.state,
                   t.setup_minutes, t.teardown_minutes
            FROM bookings b JOIN grounds g ON g.id = b.ground_id JOIN event_types t ON t.id = b.event_type_id
            WHERE b.venue_id = ?""";

      private final JdbcClient jdbc;

    public BookingRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /** A new requested booking; its database id. */
    public long insert(long venueId, String ground, String event, String club, Instant from, Instant to,
                       Instant requestedAt, Instant holdUntil) {
        KeyHolder key = new GeneratedKeyHolder();
                jdbc.sql("""
                        INSERT INTO bookings (venue_id, ground_id, event_type_id, club_name, from_at, to_at, state,
                                              requested_at, hold_until)
                        SELECT ?, g.id, t.id, ?, ?, ?, 'requested', ?, ?
                        FROM grounds g JOIN event_types t ON t.venue_id = g.venue_id
                        WHERE g.venue_id = ? AND g.code = ? AND t.code = ?""")
                .params(venueId, club, from, to, requestedAt, holdUntil, venueId, ground, event).update(key, "id");
                        return key.getKeyAs(Long.class);
    }

        public void confirm(long id, Instant at) {
        jdbc.sql("UPDATE bookings SET state = 'confirmed', confirmed_at = ? WHERE id = ?").params(at, id).update();
    }

    public void cancel(long id, Instant at, boolean late) {
        jdbc.sql("UPDATE bookings SET state = 'cancelled', cancelled_at = ?, late_cancel = ? WHERE id = ?")
                .params(at, late, id).update();
    }

    public Optional<Row> find(long venueId, long id) {
        return jdbc.sql(SELECT + " AND b.id = ?").params(venueId, id).query(Row.class).optional();
    }

    /** A ground's bookings that start on this date (UTC), in `from` order. */
    public List<Row> onDay(long venueId, String ground, LocalDate day) {
              return jdbc.sql(SELECT + " AND g.code = ? AND b.from_at >= ? AND b.from_at < ? ORDER BY b.from_at")
                .params(venueId, ground, day.atStartOfDay().toInstant(ZoneOffset.UTC),
                        day.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC))
                .query(Row.class).list();
    }
}