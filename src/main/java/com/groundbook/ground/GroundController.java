package com.groundbook.ground;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.groundbook.error.ProblemExamples;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/** The grounds, the event types and a ground's day. Parses the request and delegates; no rules live here. */
@RestController
@Tag(name = "grounds", description = "The grounds, the event types and a ground's day")
@RequestMapping("/venues/{venue}")
public class GroundController {

    private final GroundService grounds;

        public GroundController(GroundService grounds) {
        this.grounds = grounds;
    }

        @GetMapping("/grounds")
        @Operation(summary = "The venue's grounds and their opening hours")
    @ApiResponse(responseCode = "200", description = "The grounds")
        @ApiResponse(responseCode = "404", description = "No such venue",
        content = @Content(mediaType = ProblemExamples.PROBLEM, examples = {
                    @ExampleObject(name = "no-such-venue", value = ProblemExamples.NO_SUCH_VENUE)}))
    public GroundsView grounds(@PathVariable String venue) {
        return grounds.grounds(venue);
    }

        @GetMapping("/event-types")
        @Operation(summary = "The event types and their buffers")
    @ApiResponse(responseCode = "200", description = "The event types")
    @ApiResponse(responseCode = "404", description = "No such venue",
            content = @Content(mediaType = ProblemExamples.PROBLEM, examples = {
                    @ExampleObject(name = "no-such-venue", value = ProblemExamples.NO_SUCH_VENUE)}))
    public EventTypesView eventTypes(@PathVariable String venue) {
        return grounds.eventTypes(venue);
    }

        @GetMapping("/grounds/{ground}/day")
        @Operation(summary = "A ground's day: its hours for that weekday and its bookings with their shadows")
    @ApiResponse(responseCode = "200", description = "The bookings in `from` order")
        @ApiResponse(responseCode = "400", description = "A date that does not parse",
            content = @Content(mediaType = ProblemExamples.PROBLEM, examples = {
                    @ExampleObject(name = "validation", value = ProblemExamples.VALIDATION_DATE)}))
            @ApiResponse(responseCode = "404", description = "No such ground, or no such venue",
            content = @Content(mediaType = ProblemExamples.PROBLEM, examples = {
                    @ExampleObject(name = "no-such-ground", value = ProblemExamples.NO_SUCH_GROUND),
                    @ExampleObject(name = "no-such-venue", value = ProblemExamples.NO_SUCH_VENUE)}))
    public DayView day(@PathVariable String venue, @PathVariable String ground,
                       @RequestParam(required = false) LocalDate date) {
                                return grounds.day(venue, ground, date);
    }
}