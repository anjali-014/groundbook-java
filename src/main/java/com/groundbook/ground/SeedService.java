package com.groundbook.ground;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.groundbook.config.GroundBookSettings;

/**
 * Loads the Deccan venue, its four grounds (seed/grounds.csv) and its six event types with their buffers
 * (seed/event_types.csv) - once. A second start finds the venue and adds nothing.
 */
 @Service
public class SeedService {

    static final String VENUE = "deccan";

    private final VenueRepository venues;
    private final GroundRepository grounds;
    private final EventTypeRepository eventTypes;
    private final GroundBookSettings settings;

        public SeedService(VenueRepository venues, GroundRepository grounds, EventTypeRepository eventTypes,
                       GroundBookSettings settings) {
        this.venues = venues;
        this.grounds = grounds;
        this.eventTypes = eventTypes;
        this.settings = settings;
    }

        @Transactional
    public void seed() {
        if (venues.idOf(VENUE).isPresent()) {
            return;
        }
        long venue = venues.insert(VENUE, "Deccan");
        for (String[] row : csv("grounds.csv")) {        // code,name,opens,closes,sunday_opens,sunday_closes
            grounds.insert(venue, row[0], row[1], LocalTime.parse(row[2]), LocalTime.parse(row[3]),
                    LocalTime.parse(row[4]), LocalTime.parse(row[5]));
        }
                for (String[] row : csv("event_types.csv")) {      // code,name,setup_minutes,teardown_minutes
            eventTypes.insert(venue, row[0], row[1], Integer.parseInt(row[2]), Integer.parseInt(row[3]));
        }
    }

        /** The rows of a seed file, header skipped, each split on commas. */
    private List<String[]> csv(String file) {
        try {
            return Files.readAllLines(Path.of(settings.seedDir(), file)).stream().skip(1)
                    .filter(line -> !line.isBlank()).map(line -> line.trim().split("\\s*,\\s*")).toList();
                            } catch (IOException e) {
            throw new UncheckedIOException("cannot read the seed file " + file, e);
        }
    }
}