package com.groundbook.ground;

import java.time.Instant;

/** A slot's shadow: the ground held from setup before `from` until teardown after `to`. */
public record Shadow(Instant from, Instant to) {

    /**
     * The one clash predicate. Half-open: two shadows clash when each starts 
     * before the other ends, so shadows that only touch do not.
     */
         public boolean clashes(Shadow other) {
        return from.isBefore(other.to) && other.from.isBefore(to);
    }
}