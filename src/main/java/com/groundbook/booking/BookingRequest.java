package com.groundbook.booking;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** POST /bookings: which ground, which event, from when to when, and for which club. */
public record BookingRequest(@NotBlank String ground, @NotBlank String event, @NotNull Instant from,
                                 @NotNull Instant to, @NotBlank String club) {
}