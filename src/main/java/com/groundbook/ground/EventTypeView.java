package com.groundbook.ground;

/** One event type and its buffers: the minutes the ground is held before (setup) and after (teardown) a slot. */
public record EventTypeView(String code, String name, int setupMinutes, int teardownMinutes) {
}