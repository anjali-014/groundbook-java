package com.groundbook;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/** The grounds, the event types and a day, read over HTTP through MockMvc on the whole app. */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:day-test")
@AutoConfigureMockMvc
class DayTest {

    @Autowired
    MockMvc http;

    @Test
    void fourGroundsAndSixEventTypes() throws Exception {
              http.perform(get("/venues/deccan/grounds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grounds.length()").value(4))
                .andExpect(jsonPath("$.grounds[0].opens").value("06:00"));
                        http.perform(get("/venues/deccan/event-types"))
                .andExpect(jsonPath("$.eventTypes.length()").value(6))
                .andExpect(jsonPath("$.eventTypes[?(@.code == 'league')].setupMinutes").value(40));
    }

        @Test
    void aSaturdayRunsSixToTenAndASundaySevenToEight() throws Exception {
        http.perform(get("/venues/deccan/grounds/A/day").param("date", "2026-10-03"))
                .andExpect(jsonPath("$.opens").value("06:00"))
                .andExpect(jsonPath("$.closes").value("22:00"));
                        http.perform(get("/venues/deccan/grounds/A/day").param("date", "2026-10-04"))
                .andExpect(jsonPath("$.opens").value("07:00"))
                .andExpect(jsonPath("$.closes").value("20:00"));
    }

        @Test
    void aBadDateIs400AndAnUnknownGroundOrVenueIs404() throws Exception {
        http.perform(get("/venues/deccan/grounds/A/day").param("date", "2026-13-40"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("/errors/validation"));
                        http.perform(get("/venues/deccan/grounds/Z/day"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("/errors/no-such-ground"));
                        http.perform(get("/venues/mumbai/grounds"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("/errors/no-such-venue"));
    }
}