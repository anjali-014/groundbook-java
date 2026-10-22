package com.groundbook.ground;

import java.util.List;

/** GET /venues/{venue}/event-types. */
public record EventTypesView(List<EventTypeView> eventTypes) {
}