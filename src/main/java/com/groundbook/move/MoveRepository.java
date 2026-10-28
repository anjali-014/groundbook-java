package com.groundbook.move;

import java.time.Instant;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/** The move's one write: the old interval released and the new one taken in a single UPDATE of the booking. */
@Repository
public class MoveRepository {

    private final JdbcClient jdbc;

    public MoveRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public void move(long bookingId, Instant from, Instant to, Instant movedAt) {
                jdbc.sql("""
                        UPDATE bookings SET previous_from = from_at, previous_to = to_at, from_at = ?, to_at = ?,
                                    moved_at = ?
                        WHERE id = ?""")
                .params(from, to, movedAt, bookingId).update();
                    }
}