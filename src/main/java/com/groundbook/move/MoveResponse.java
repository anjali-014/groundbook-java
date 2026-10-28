package com.groundbook.move;

import java.time.Instant;

/** POST /bookings/{bookingId}/move 200: the new interval, the ground it now holds, and when it moved. */
public record MoveResponse(String bookingId, Instant from, Instant to, Instant groundHeldFrom, Instant groundHeldTo,
                            Instant movedAt) {
}