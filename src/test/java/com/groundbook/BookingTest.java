package com.groundbook;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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

/** Rafi's club asks for Saturday two till five, with the clock fixed on the Thursday before. */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:booking-test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(FixedClockConfig.class)
class BookingTest {

    @Autowired
    MockMvc http;
    ResultActions request(String from, String to) throws Exception {
    return http.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON).content(
                "{\"ground\":\"A\",\"event\":\"league\",\"from\":\"2026-10-03T" + from + ":00Z\",\"to\":\"2026-10-03T"
                        + to + ":00Z\",\"club\":\"Ambedkar Nagar CC\"}"));
    }


    @Test
    void twoTillFiveIsRequestedAndHeldFrom1320() throws Exception {
        request("14:00", "17:00")
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/bookings/BK0001"))
                .andExpect(jsonPath("$.groundHeldFrom").value("2026-10-03T13:20:00Z"))
                .andExpect(jsonPath("$.holdUntil").value("2026-10-02T08:00:00Z"));
                        http.perform(get("/venues/deccan/grounds/A/day").param("date", "2026-10-03"))
                .andExpect(jsonPath("$.bookings[0].bookingId").value("BK0001"));
    }

        @Test
    void sixTwentyIsHeldFromFiveFortyBeforeTheGroundOpens() throws Exception {
              request("06:20", "08:00")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("/errors/bad-interval"))
                .andExpect(jsonPath("$.opens").value("06:00"));
    }

    @Test
    void fiveTillTwoIsABadIntervalAndAMissingFromIsAValidationError() throws Exception {
        request("17:00", "14:00").andExpect(jsonPath("$.type").value("/errors/bad-interval"));
        http.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ground\":\"A\",\"event\":\"league\",\"to\":\"2026-10-03T17:00:00Z\","
                                + "\"club\":\"X\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("/errors/validation"))
                .andExpect(jsonPath("$.errors[0].field").value("from"));
                    }
}