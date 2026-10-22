package com.groundbook.ground;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/** GET /venues/{venue}/grounds/{ground}/day: the hours for that weekday (06:00) and the bookings, in `from` order. */
public record DayView(String venue, String ground, LocalDate date, @JsonFormat(pattern = "HH:mm") LocalTime opens,
                      @JsonFormat(pattern = "HH:mm") LocalTime closes, List<DayBooking> bookings) {
}