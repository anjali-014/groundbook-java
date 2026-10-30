package com.groundbook;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;

/** The same table and rows, with each event type's own buffers from the seed. */
class RuleTablePerTypeBuffersTest extends RuleTable {

        @Override
    int column() {
        return 1;
    }

        @Test
    void settingsShowTheHoldAndThePerTypeBuffers() throws Exception {
        http.perform(get("/settings"))
                .andExpect(jsonPath("$.holdHours").value(24))
                                .andExpect(jsonPath("$.buffers.practice[0]").value(20))
                .andExpect(jsonPath("$.buffers.school_day[1]").value(120));
    }
}