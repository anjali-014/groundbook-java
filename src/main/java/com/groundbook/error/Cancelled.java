package com.groundbook.error;

/** 409 /errors/cancelled: a cancelled booking cannot be confirmed or moved. */
public class Cancelled extends DomainException {

        public Cancelled(String bookingId) {
        super(409, "/errors/cancelled", "Cancelled", "Booking " + bookingId + " is cancelled; request a new one");
    }
}