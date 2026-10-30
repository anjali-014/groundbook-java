package com.groundbook.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.groundbook.ground.GroundService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/** The live settings, read-only - the rule book screen reads them from here. */
@RestController
@Tag(name = "settings", description = "The rules the app is running with")
public class SettingsController {

        static final String VENUE = "deccan";

    private final GroundBookSettings settings;
    private final GroundService grounds;

        public SettingsController(GroundBookSettings settings, GroundService grounds) {
        this.settings = settings;
        this.grounds = grounds;
    }

        @GetMapping("/settings")
    @Operation(summary = "The hold, and the live buffer table from the event types")
    @ApiResponse(responseCode = "200", description = "The current settings")
    public SettingsView settings() {
                return new SettingsView(settings.holdHours(), grounds.buffers(VENUE));
    }
}