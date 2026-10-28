package com.groundbook;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.groundbook.move.MoveRepository;

/** A slot cannot move into the next booking's buffer, can move an hour earlier, and a failed move stays put. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.datasource.url=jdbc:h2:mem:move-test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(FixedClockConfig.class)
class MoveTest {
 
    @Autowired
    TestRestTemplate http;

    @MockitoSpyBean
    MoveRepository moves;

    private void request(String from, String to) {
        http.postForObject("/bookings", Map.of("ground", "A", "event", "league", "from", "2026-10-03T" + from + ":00Z",
                "to", "2026-10-03T" + to + ":00Z", "club", "Ambedkar Nagar CC"), Map.class);
    }

    private int move(String id, String from, String to) {
        return http.postForEntity("/bookings/" + id + "/move", Map.of("from", "2026-10-03T" + from + ":00Z",
                "to", "2026-10-03T" + to + ":00Z"), String.class).getStatusCode().value();
    }

    private Object from(String id) {
        return http.getForObject("/bookings/" + id, Map.class).get("from");
    }

    @Test
    void anHourLaterClashesAndStaysPutButAnHourEarlierMoves() {
        request("14:00", "17:00");
        request("18:30", "20:30");
        assertThat(move("BK0001", "15:00", "18:00")).isEqualTo(409);
        assertThat(from("BK0001")).isEqualTo("2026-10-03T14:00:00Z");
        assertThat(move("BK0001", "13:00", "16:00")).isEqualTo(200);
        assertThat(from("BK0001")).isEqualTo("2026-10-03T13:00:00Z");
    }

    @Test
    void aMoveThatFailsPartWayLeavesTheSlotAtTwo() {
        request("14:00", "17:00");
        doThrow(new IllegalStateException("injected: the move cannot be written")).when(moves)
                .move(anyLong(), any(), any(), any());
                        assertThat(move("BK0001", "15:00", "18:00")).isEqualTo(500);
        assertThat(from("BK0001")).isEqualTo("2026-10-03T14:00:00Z");
    }
}