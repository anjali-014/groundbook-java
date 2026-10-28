package com.groundbook.error;

/** 409 /errors/already-over: the interval has ended. */
public class AlreadyOver extends DomainException {

        public AlreadyOver(String bookingId) {
        super(409, "/errors/already-over", "Already over",
                "Booking " + bookingId + " has ended; it can no longer be cancelled");
    }
}