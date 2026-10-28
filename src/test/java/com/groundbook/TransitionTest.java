package com.groundbook;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 *  Confirm and cancel, with the clock starting on Thursday 1 October at 08:00 and moved to Saturday when a rule
 * needs the slot to have begun or ended.
 */
 @SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:transition-test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(MovableClockConfig.class)
class TransitionTest {

        @Autowired
    MockMvc http;

    @Autowired
    Clock clock;

        ResultActions request(String from, String to) throws Exception {
       return http.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON).content(
                "{\"ground\":\"A\",\"event\":\"league\",\"from\":\"2026-10-03T" + from + ":00Z\",\"to\":\"2026-10-03T"
                        + to + ":00Z\",\"club\":\"Ambedkar Nagar CC\"}"));
    }

        void moveTo(String saturdayTime) {
        MovableClock movable = (MovableClock) clock;
        movable.advance(Duration.between(movable.instant(), Instant.parse("2026-10-03T" + saturdayTime + ":00Z")));
    }

        @Test
    void confirmBeforeTheSlotThenItHasBegunAndThenItIsOver() throws Exception {
        request("14:00", "17:00").andExpect(status().isCreated());
                http.perform(post("/bookings/BK0001/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.confirmedAt").value("2026-10-01T08:00:00Z"));
                        request("19:00", "20:00").andExpect(status().isCreated());
        moveTo("19:30");
        http.perform(post("/bookings/BK0002/confirm"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("/errors/already-started"));
                        http.perform(post("/bookings/BK0001/cancel")).andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("/errors/already-over"));
    }

        @Test
    void cancellingTwentyMinutesInIsLate() throws Exception {
        request("14:00", "17:00").andExpect(status().isCreated());
        moveTo("14:20");
                http.perform(post("/bookings/BK0001/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.late").value(true));
    }
}