package com.groundbook.booking;

import java.time.Instant;

/** POST /bookings 201: the booking, the ground it holds (its shadow), and how long the request holds it. */
public record RequestedBooking(String bookingId, String ground, String event, Instant from, Instant to,
                                   Instant groundHeldFrom, Instant groundHeldTo, String state, Instant holdUntil) {
}