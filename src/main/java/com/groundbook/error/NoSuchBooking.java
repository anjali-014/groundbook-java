package com.groundbook.error;

/** 404 /errors/no-such-booking. */
public class NoSuchBooking extends DomainException {

      public NoSuchBooking(String bookingId) {
        super(404, "/errors/no-such-booking", "No such booking", "There is no booking " + bookingId);
    }
}