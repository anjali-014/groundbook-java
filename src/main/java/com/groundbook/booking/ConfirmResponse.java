package com.groundbook.booking;

import java.time.Instant;

/** POST /bookings/{bookingId}/confirm 200. */
public record ConfirmResponse(String bookingId, String state, Instant confirmedAt) {
}