package com.groundbook.booking;

import java.time.Instant;

/** POST /bookings/{bookingId}/cancel 200: `late` when the slot had already begun. */
public record CancelResponse(String bookingId, String state, Instant cancelledAt, boolean late) {
}