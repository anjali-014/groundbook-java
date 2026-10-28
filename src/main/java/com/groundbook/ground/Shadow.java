package com.groundbook.ground;

import java.time.Instant;

/** A slot's shadow: the ground held from setup before `from` until teardown after `to`. */
public record Shadow(Instant from, Instant to) {
}