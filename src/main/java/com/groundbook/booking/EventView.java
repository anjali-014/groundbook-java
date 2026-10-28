package com.groundbook.booking;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * O One line of a booking's history: when, what, and that event's own facts (the interval requested, the old and new
 * intervals of a move, whether a cancel was late), which appear next to `at` and `kind` in the JSON.
 */
 @JsonPropertyOrder({"at", "kind"})
public record EventView(Instant at, String kind, @JsonAnyGetter Map<String, Object> facts) {
}