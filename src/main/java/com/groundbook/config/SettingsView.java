package com.groundbook.config;

import java.util.List;
import java.util.Map;

/** GET /settings: the hold, and each event type's buffers as [setup, teardown] minutes. */
public record SettingsView(int holdHours, Map<String, List<Integer>> buffers) {
}