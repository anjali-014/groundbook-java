package com.groundbook.error;

/** 404 /errors/no-such-ground. */
public class NoSuchGround extends DomainException {

      public NoSuchGround(String venue, String ground) {
        super(404, "/errors/no-such-ground", "No such ground", "There is no ground " + ground + " at " + venue);
    }
}