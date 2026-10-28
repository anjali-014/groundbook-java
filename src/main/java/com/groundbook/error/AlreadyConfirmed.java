package com.groundbook.error;

/** 409 /errors/already-confirmed: the booking is confirmed already. */
public class AlreadyConfirmed extends DomainException {

        public AlreadyConfirmed(String bookingId) {
        super(409, "/errors/already-confirmed", "Already confirmed", "Booking " + bookingId + " is already confirmed");
    }
}