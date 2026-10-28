package com.groundbook.error;

/** 409 /errors/already-started: the interval has begun. */
public class AlreadyStarted extends DomainException {

        public AlreadyStarted(String bookingId) {
        super(409, "/errors/already-started", "Already started",
                "Booking " + bookingId + " has begun; it can no longer be confirmed or moved");
    }
}