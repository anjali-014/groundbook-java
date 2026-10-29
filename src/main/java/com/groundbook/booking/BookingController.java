package com.groundbook.booking;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.groundbook.error.ProblemExamples;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/** Bookings. There is one venue, Deccan. Parses the request and delegates; no rules live here. */
@RestController
@Tag(name = "bookings", description = "A club's slots: request, confirm, cancel")
public class BookingController {

    static final String VENUE = "deccan";

    private final BookingService bookings;

    public BookingController(BookingService bookings) {
        this.bookings = bookings;
    }

    @PostMapping("/bookings")
    @Operation(summary = "Request a slot: the booking is held for HOLD_HOURS until it is confirmed")
    @ApiResponse(responseCode = "201", description = "Requested; the booking is at the Location given")
    @ApiResponse(responseCode = "400", description = "Not valid, an impossible interval, or no such event type",
            content = @Content(mediaType = ProblemExamples.PROBLEM, examples = {
                    @ExampleObject(name = "validation", value = ProblemExamples.VALIDATION_REQUEST),
                    @ExampleObject(name = "bad-interval", value = ProblemExamples.BAD_INTERVAL),
                    @ExampleObject(name = "no-such-event-type", value = ProblemExamples.NO_SUCH_EVENT_TYPE)}))
            @ApiResponse(responseCode = "404", description = "No such ground",
            content = @Content(mediaType = ProblemExamples.PROBLEM, examples = {
                    @ExampleObject(name = "no-such-ground", value = ProblemExamples.NO_SUCH_GROUND_REQUEST)}))
            @ApiResponse(responseCode = "409", description = "The shadow clashes with another booking's",
            content = @Content(mediaType = ProblemExamples.PROBLEM,
                    examples = @ExampleObject(name = "overlap", value = ProblemExamples.OVERLAP)))
    public ResponseEntity<RequestedBooking> request(@Valid @RequestBody BookingRequest body) {
        RequestedBooking made = bookings.request(VENUE, body.ground(), body.event(), body.from(), body.to(),
                body.club());
      return ResponseEntity.created(URI.create("/bookings/" + made.bookingId())).body(made);
    }

    @PostMapping("/bookings/{bookingId}/confirm")
        @Operation(summary = "Confirm a requested booking before its slot begins")
    @ApiResponse(responseCode = "200", description = "Confirmed")
    @ApiResponse(responseCode = "404", description = "No such booking",
            content = @Content(mediaType = ProblemExamples.PROBLEM, examples = {
                    @ExampleObject(name = "no-such-booking", value = ProblemExamples.NO_SUCH_BOOKING_CONFIRM)}))
                        @ApiResponse(responseCode = "409", description = "Begun, cancelled, already confirmed, or now clashing",
            content = @Content(mediaType = ProblemExamples.PROBLEM, examples = {
                    @ExampleObject(name = "already-started", value = ProblemExamples.ALREADY_STARTED),
                    @ExampleObject(name = "cancelled", value = ProblemExamples.CANCELLED),
                    @ExampleObject(name = "already-confirmed", value = ProblemExamples.ALREADY_CONFIRMED),
                    @ExampleObject(name = "overlap", value = ProblemExamples.OVERLAP_CONFIRM)}))
    public ConfirmResponse confirm(@PathVariable String bookingId) {
                return bookings.confirm(VENUE, bookingId);
    }

    @PostMapping("/bookings/{bookingId}/cancel")
    @Operation(summary = "Cancel a booking before its slot ends; after it begins the cancel is late")
    @ApiResponse(responseCode = "200", description = "Cancelled")
    @ApiResponse(responseCode = "404", description = "No such booking",
            content = @Content(mediaType = ProblemExamples.PROBLEM, examples = {
                    @ExampleObject(name = "no-such-booking", value = ProblemExamples.NO_SUCH_BOOKING_CANCEL)}))
                        @ApiResponse(responseCode = "409", description = "Over, or already cancelled",
            content = @Content(mediaType = ProblemExamples.PROBLEM, examples = {
                    @ExampleObject(name = "already-over", value = ProblemExamples.ALREADY_OVER),
                    @ExampleObject(name = "already-cancelled", value = ProblemExamples.ALREADY_CANCELLED)}))
    public CancelResponse cancel(@PathVariable String bookingId) {
        return bookings.cancel(VENUE, bookingId);
    }

        @GetMapping("/bookings/{bookingId}")
    @Operation(summary = "One booking: its slot, its shadow, its state and its history")
    @ApiResponse(responseCode = "200", description = "The booking")
    @ApiResponse(responseCode = "404", description = "No such booking",
            content = @Content(mediaType = ProblemExamples.PROBLEM, examples = {
                    @ExampleObject(name = "no-such-booking", value = ProblemExamples.NO_SUCH_BOOKING)}))
    public BookingView booking(@PathVariable String bookingId) {
        return bookings.booking(VENUE, bookingId);
    }
}
