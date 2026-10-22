package com.groundbook.ground;

import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/** The venues table. */
@Repository
public class VenueRepository {

    private final JdbcClient jdbc;

        public VenueRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

        /** The database id of the venue with this slug. */
    public Optional<Long> idOf(String slug) {
        return jdbc.sql("SELECT id FROM venues WHERE slug = ?").param(slug).query(Long.class).optional();
    }

        public long insert(String slug, String name) {
        jdbc.sql("INSERT INTO venues (slug, name) VALUES (?, ?)").params(slug, name).update();
        return idOf(slug).orElseThrow();
    }
}