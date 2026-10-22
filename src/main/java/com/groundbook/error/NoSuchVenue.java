package com.groundbook.error;

/** 404 /errors/no-such-venue. */
public class NoSuchVenue extends DomainException {

      public NoSuchVenue(String venue) {
        super(404, "/errors/no-such-venue", "No such venue", "There is no venue " + venue);
    }
}