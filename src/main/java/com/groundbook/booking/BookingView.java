package com.groundbook.booking;

import java.time.Instant;

/** GET /bookings/{bookingId}: the booking, the ground it holds (its shadow) and its state. */
public record BookingView(String bookingId, String ground, String event, Instant from, Instant to,
                              Instant groundHeldFrom, Instant groundHeldTo, String state) {
}