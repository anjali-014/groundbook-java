package com.groundbook;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The rule table. One row per case (src/test/resources/rules.csv): its setup, its action, and the status and
 * type expected with a uniform 40/30 buffer table and with each event type's own buffers. Every row runs on a fresh
 * venue, on the whole app through MockMvc, with a movable clock starting on Thursday 1 October at 08:00. The two
 * subclasses run the same table against each buffer table.
 */
 @SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:rule-table")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(MovableClockConfig.class)
abstract class RuleTable {

        private static final ObjectMapper JSON = new ObjectMapper();

    @Autowired
    MockMvc http;

        @Autowired
    Clock clock;

    /** Which column this run checks: 0 uniform 40/30, 1 each event type's own buffers. */
    abstract int column();

        @ParameterizedTest(name = "{0} [{1}]")
    @CsvFileSource(resources = "/rules.csv", numLinesToSkip = 1)
    void rule(String name, String tag, String setup, String action, int statusUniform, String typeUniform,
              int statusPerType, String typePerType) throws Exception {
                        List<String> ids = new ArrayList<>();
        for (String text : setup == null ? new String[0] : setup.split(";")) {
            MockHttpServletResponse r = step(text, ids);
            assertThat(r == null || r.getStatus() < 300).as(name + " setup " + text).isTrue();
        }
                MockHttpServletResponse answer = step(action, ids);
        int[] statuses = {statusUniform, statusPerType};
        String[] types = {typeUniform, typePerType};
        assertThat(answer.getStatus()).as(name + " status").isEqualTo(statuses[column()]);
                if (types[column()] != null) {
            assertThat(JSON.readTree(answer.getContentAsString()).path("type").asText()).as(name + " type")
                    .isEqualTo(types[column()]);
        }
    }

        private MockHttpServletResponse step(String text, List<String> ids) throws Exception {
        String[] w = text.trim().split(" ");
        return switch (w[0]) {
                        case "request" -> {
                MockHttpServletResponse r = send("/bookings", "{\"ground\":\"" + w[1] + "\",\"event\":\"" + w[2]
                        + "\",\"from\":\"" + sat(w[3]) + "\",\"to\":\"" + sat(w[4])
                        + "\",\"club\":\"Ambedkar Nagar CC\"}");
                                        if (r.getStatus() == 201) {
                    ids.add(JSON.readTree(r.getContentAsString()).path("bookingId").asText());
                }
                yield r;
            }
                        case "request_without_from" -> send("/bookings", "{\"ground\":\"" + w[1] + "\",\"event\":\"" + w[2]
                    + "\",\"to\":\"" + sat(w[3]) + "\",\"club\":\"Ambedkar Nagar CC\"}");
            case "confirm", "cancel" -> send("/bookings/" + ref(w[1], ids) + "/" + w[0], null);
                        case "move" -> send("/bookings/" + ref(w[1], ids) + "/move",
                    "{\"from\":\"" + sat(w[2]) + "\",\"to\":\"" + sat(w[3]) + "\"}");
            case "day" -> http.perform(get("/venues/deccan/grounds/" + w[1] + "/day").param("date", w[2]))
                    .andReturn().getResponse();
            case "grounds" -> http.perform(get("/venues/" + w[1] + "/grounds")).andReturn().getResponse();
                        case "clock" -> {
                MovableClock movable = (MovableClock) clock;
                movable.advance(Duration.between(movable.instant(), sat(w[2])));
                yield null;
            }
                        default -> throw new IllegalArgumentException("unknown step " + text);
        };
    }

        private static Instant sat(String hhmm) {
        return Instant.parse("2026-10-03T" + hhmm + ":00Z");
    }

        /** A booking named by its place in this row's requests (1 is the first), or by its id. */
    private static String ref(String r, List<String> ids) {
        return r.startsWith("BK") ? r : ids.get(Integer.parseInt(r) - 1);
    }

        private MockHttpServletResponse send(String path, String body) throws Exception {
        var request = post(path);
        if (body != null) {
            request = request.contentType(MediaType.APPLICATION_JSON).content(body);
                    }
        return http.perform(request).andReturn().getResponse();
    }
}