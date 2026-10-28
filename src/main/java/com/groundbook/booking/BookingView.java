package com.groundbook.booking;

import java.time.Instant;
import java.util.List;

/** GET /bookings/{bookingId}: the booking, the ground it holds (its shadow), its state and its history. */
public record BookingView(String bookingId, String ground, String event, Instant from, Instant to,
            Instant groundHeldFrom, Instant groundHeldTo, String state, List<EventView> events) {
}