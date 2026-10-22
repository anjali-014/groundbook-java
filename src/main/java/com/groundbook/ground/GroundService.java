package com.groundbook.ground;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.stereotype.Service;

import com.groundbook.error.NoSuchGround;
import com.groundbook.error.NoSuchVenue;

/** The venue's grounds, its event types, and a ground's day with its hours for that weekday. Times are UTC. */
@Service
public class GroundService {
  
    private final VenueRepository venues;
    private final GroundRepository grounds;
    private final EventTypeRepository eventTypes;
    private final Clock clock;

        public GroundService(VenueRepository venues, GroundRepository grounds, EventTypeRepository eventTypes,
                         Clock clock) {
        this.venues = venues;
        this.grounds = grounds;
        this.eventTypes = eventTypes;
        this.clock = clock;
    }

    public GroundsView grounds(String venue) {
        return new GroundsView(venue, grounds.all(venueId(venue)).stream()
                .map(g -> new GroundView(g.code(), g.name(), g.opens(), g.closes(), g.sundayOpens(), g.sundayCloses()))
                .toList());
    }

        public EventTypesView eventTypes(String venue) {
        return new EventTypesView(eventTypes.all(venueId(venue)).stream()
                .map(t -> new EventTypeView(t.code(), t.name(), t.setupMinutes(), t.teardownMinutes())).toList());
        }

        /** A ground's day - by default today by the clock - with that weekday's hours. */
    public DayView day(String venue, String ground, LocalDate date) {
        GroundRepository.Row row = grounds.one(venueId(venue), ground)
                .orElseThrow(() -> new NoSuchGround(venue, ground));
        LocalDate day = date != null ? date : LocalDate.ofInstant(clock.instant(), ZoneOffset.UTC);
        LocalTime[] hours = hours(row, day);
        return new DayView(venue, ground, day, hours[0], hours[1], List.of());
    }

        /** Opening hours depend on the weekday: Sunday uses the sunday_* columns. */
    static LocalTime[] hours(GroundRepository.Row ground, LocalDate day) {
        return day.getDayOfWeek() == DayOfWeek.SUNDAY
                ? new LocalTime[] {ground.sundayOpens(), ground.sundayCloses()}
                : new LocalTime[] {ground.opens(), ground.closes()};
                    }

    private long venueId(String venue) {
        return venues.idOf(venue).orElseThrow(() -> new NoSuchVenue(venue));
    }
}
   