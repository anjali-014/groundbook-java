package com.groundbook.ground;

import java.time.Instant;

/** One booking on a ground's day, with its shadow: the ground held from setup before to teardown after. */
public record DayBooking(String bookingId, String event, Instant from, Instant to, Instant groundHeldFrom,
                             Instant groundHeldTo, String state) {
}