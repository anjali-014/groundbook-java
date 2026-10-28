package com.groundbook.move;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;

/** POST /bookings/{bookingId}/move: the new interval. */
public record MoveRequest(@NotNull Instant from, @NotNull Instant to) {
}
