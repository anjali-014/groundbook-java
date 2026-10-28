package com.groundbook.move;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/** Moves. There is one venue, Deccan. Parses the request and delegates; no rules live here. */
@RestController
public class MoveController {

        static final String VENUE = "deccan";

    private final MoveService moves;

    public MoveController(MoveService moves) {
        this.moves = moves;
    }

    @PostMapping("/bookings/{bookingId}/move")
    public MoveResponse move(@PathVariable String bookingId, @Valid @RequestBody MoveRequest body) {
        return moves.move(VENUE, bookingId, body.from(), body.to());
    }
}