package com.groundbook.move;

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

/** Moves. There is one venue, Deccan. Parses the request and delegates; no rules live here. */
@RestController
@Tag(name = "moves", description = "Move a booking to a new interval, in one transaction")
public class MoveController {

        static final String VENUE = "deccan";

    private final MoveService moves;

    public MoveController(MoveService moves) {
        this.moves = moves;
    }

    @PostMapping("/bookings/{bookingId}/move")
    @Operation(summary = "Move a booking: the whole new interval and both buffers are checked first")
    @ApiResponse(responseCode = "200", description = "Moved")
        @ApiResponse(responseCode = "400", description = "An impossible interval, or a body that is not valid",
            content = @Content(mediaType = ProblemExamples.PROBLEM, examples = {
                    @ExampleObject(name = "bad-interval", value = ProblemExamples.BAD_INTERVAL_MOVE),
                    @ExampleObject(name = "validation", value = ProblemExamples.VALIDATION_MOVE)}))
        @ApiResponse(responseCode = "404", description = "No such booking",
            content = @Content(mediaType = ProblemExamples.PROBLEM, examples = {
                    @ExampleObject(name = "no-such-booking", value = ProblemExamples.NO_SUCH_BOOKING_MOVE)}))
         @ApiResponse(responseCode = "409", description = "The new shadow clashes, the slot has begun, or it is cancelled",
            content = @Content(mediaType = ProblemExamples.PROBLEM, examples = {
                    @ExampleObject(name = "overlap", value = ProblemExamples.OVERLAP_MOVE),
                    @ExampleObject(name = "already-started", value = ProblemExamples.ALREADY_STARTED_MOVE),
                    @ExampleObject(name = "cancelled", value = ProblemExamples.CANCELLED_MOVE)}))
    public MoveResponse move(@PathVariable String bookingId, @Valid @RequestBody MoveRequest body) {
        return moves.move(VENUE, bookingId, body.from(), body.to());
    }
}