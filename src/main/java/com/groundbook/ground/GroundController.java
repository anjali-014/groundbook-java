package com.groundbook.ground;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** The grounds, the event types and a ground's day. Parses the request and delegates; no rules live here. */
@RestController
@RequestMapping("/venues/{venue}")
public class GroundController {

    private final GroundService grounds;

        public GroundController(GroundService grounds) {
        this.grounds = grounds;
    }

        @GetMapping("/grounds")
    public GroundsView grounds(@PathVariable String venue) {
        return grounds.grounds(venue);
    }

        @GetMapping("/event-types")
    public EventTypesView eventTypes(@PathVariable String venue) {
        return grounds.eventTypes(venue);
    }

        @GetMapping("/grounds/{ground}/day")
    public DayView day(@PathVariable String venue, @PathVariable String ground,
                       @RequestParam(required = false) LocalDate date) {
                                return grounds.day(venue, ground, date);
    }
}