package com.groundbook;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;

/** Every request and every event leaves one line on stdout, and no line ever holds a club's name. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.datasource.url=jdbc:h2:mem:log-test")
@ExtendWith(OutputCaptureExtension.class)
@Import(FixedClockConfig.class)
class LogTest {

    @Autowired
    TestRestTemplate http;

        @Test
    void aRequestAndAConfirmAreLoggedWithoutTheClub(CapturedOutput output) {
                http.postForObject("/bookings", Map.of("ground", "A", "event", "league", "from", "2026-10-03T14:00:00Z",
                "to", "2026-10-03T17:00:00Z", "club", "Ambedkar Nagar CC"), Map.class);
                        assertThat(http.postForEntity("/bookings/BK0001/confirm", null, String.class).getStatusCode().value())
                .isEqualTo(200);

                        String out = output.getOut();
        assertThat(out.lines().filter(l -> l.contains("method=POST") && l.contains("path=/bookings "))).hasSize(1);
        assertThat(out.lines().filter(l -> l.contains("path=/bookings/BK0001/confirm"))).hasSize(1);
                assertThat(out).contains("kind=requested", "kind=confirmed");
        assertThat(output.getAll()).doesNotContain("Ambedkar");
    }
}