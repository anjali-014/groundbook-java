package com.groundbook;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/** A clock fixed at 08:00 on Thursday 1 October in place of the app's own, for tests that import it. */
@TestConfiguration
public class FixedClockConfig {

    @Bean
    @Primary
    Clock fixedClock() {
              return Clock.fixed(Instant.parse("2026-10-01T08:00:00Z"), ZoneOffset.UTC);
    }
}
