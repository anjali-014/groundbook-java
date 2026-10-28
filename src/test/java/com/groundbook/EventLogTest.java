package com.groundbook;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.groundbook.booking.EventLog;

/** A booking's life is logged in order, and the log can only be added to and read. */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:event-log-test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(FixedClockConfig.class)
class EventLogTest {

    @Autowired
    MockMvc http;

    @Test
    void requestedConfirmedMovedCancelledInOrder() throws Exception {
                http.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON).content("{\"ground\":\"A\","
                        + "\"event\":\"league\",\"from\":\"2026-10-03T14:00:00Z\",\"to\":\"2026-10-03T17:00:00Z\","
                        + "\"club\":\"Ambedkar Nagar CC\"}"))
                .andExpect(status().isCreated());
                        http.perform(post("/bookings/BK0001/confirm")).andExpect(status().isOk());
        http.perform(post("/bookings/BK0001/move").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"from\":\"2026-10-03T15:00:00Z\",\"to\":\"2026-10-03T18:00:00Z\"}"))
                .andExpect(status().isOk());
        http.perform(post("/bookings/BK0001/cancel")).andExpect(status().isOk());

                http.perform(get("/bookings/BK0001"))
                .andExpect(jsonPath("$.events.length()").value(4))
                .andExpect(jsonPath("$.events[0].kind").value("requested"))
                .andExpect(jsonPath("$.events[2].kind").value("moved"))
                .andExpect(jsonPath("$.events[2].previousFrom").value("2026-10-03T14:00:00Z"))
                .andExpect(jsonPath("$.events[3].kind").value("cancelled"));
                    }

    @Test
    void theLogCanOnlyAppendAndRead() {
                String[] publicMethods = Arrays.stream(EventLog.class.getDeclaredMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()) && !m.isSynthetic())
                .map(Method::getName).sorted().toArray(String[]::new);
                        assertThat(publicMethods).containsExactly("append", "forBooking");
    }
}