package com.groundbook;

import java.time.Instant;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/** A movable clock, starting at 08:00 on Thursday 1 October, in place of the app's own, for tests that import it. */
@TestConfiguration
public class MovableClockConfig {

               @Bean
    @Primary
    MovableClock movableClock() {
                return new MovableClock(Instant.parse("2026-10-01T08:00:00Z"));
    }
}