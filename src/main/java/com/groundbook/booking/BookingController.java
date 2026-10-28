package com.groundbook.booking;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/** Bookings. There is one venue, Deccan. Parses the request and delegates; no rules live here. */
@RestController
public class BookingController {

    static final String VENUE = "deccan";

    private final BookingService bookings;

    public BookingController(BookingService bookings) {
        this.bookings = bookings;
    }

    @PostMapping("/bookings")
    public ResponseEntity<RequestedBooking> request(@Valid @RequestBody BookingRequest body) {
        RequestedBooking made = bookings.request(VENUE, body.ground(), body.event(), body.from(), body.to(),
                body.club());
      return ResponseEntity.created(URI.create("/bookings/" + made.bookingId())).body(made);
    }

    @PostMapping("/bookings/{bookingId}/confirm")
    public ConfirmResponse confirm(@PathVariable String bookingId) {
                return bookings.confirm(VENUE, bookingId);
    }

    @PostMapping("/bookings/{bookingId}/cancel")
    public CancelResponse cancel(@PathVariable String bookingId) {
        return bookings.cancel(VENUE, bookingId);
    }

        @GetMapping("/bookings/{bookingId}")
    public BookingView booking(@PathVariable String bookingId) {
        return bookings.booking(VENUE, bookingId);
    }
}
