package com.groundbook;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/** On paper two till four and four till six do not touch; with the buffers they do. */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:overlap-test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(FixedClockConfig.class)
class OverlapTest {

        @Autowired
    MockMvc http;

        ResultActions request(String from, String to) throws Exception {
        return http.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON).content(
                "{\"ground\":\"A\",\"event\":\"league\",\"from\":\"2026-10-03T" + from + ":00Z\",\"to\":\"2026-10-03T"
                        + to + ":00Z\",\"club\":\"Ambedkar Nagar CC\"}"));
    }

        @Test
    void fourTillSixClashesWithTwoTillFourAndNamesIt() throws Exception {
        request("14:00", "16:00").andExpect(status().isCreated());
                request("16:00", "18:00")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("/errors/overlap"))
                .andExpect(jsonPath("$.clashesWith[0].bookingId").value("BK0001"))
                .andExpect(jsonPath("$.clashesWith[0].groundHeldTo").value("2026-10-03T16:30:00Z"));
                        request("17:10", "19:00").andExpect(status().isCreated());
    }

    @Test
    void aCancelledBookingCannotBeConfirmedAgain() throws Exception {
        request("14:00", "16:00").andExpect(status().isCreated());
        http.perform(post("/bookings/BK0001/cancel")).andExpect(status().isOk());
                http.perform(post("/bookings/BK0001/confirm"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("/errors/cancelled"));
    }
}