package com.groundbook.error;

/** 409 /errors/already-cancelled: the booking is cancelled already. */
public class AlreadyCancelled extends DomainException {

        public AlreadyCancelled(String bookingId) {
        super(409, "/errors/already-cancelled", "Already cancelled", "Booking " + bookingId + " is already cancelled");
    }
}