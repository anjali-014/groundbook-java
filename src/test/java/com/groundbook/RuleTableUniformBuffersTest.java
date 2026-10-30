package com.groundbook;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;

/** The same table and rows, starting from event_types rows that all hold 40 minutes before and 30 after. */
class RuleTableUniformBuffersTest extends RuleTable {

    @Autowired
    JdbcClient jdbc;

        @BeforeEach
    void everyEventTypeHasTheOldUniformBuffers() {
        jdbc.sql("UPDATE event_types SET setup_minutes = 40, teardown_minutes = 30").update();
    }

        @Override
    int column() {
        return 0;
    }
}