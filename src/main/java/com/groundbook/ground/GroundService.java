package com.groundbook.ground;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;

import com.groundbook.booking.BookingRepository;
import com.groundbook.error.NoSuchGround;
import com.groundbook.error.NoSuchVenue;

/** The venue's grounds, its event types, and a ground's day with its hours for that weekday. Times are UTC. */
@Service
public class GroundService {
  
    private final VenueRepository venues;
    private final GroundRepository grounds;
    private final EventTypeRepository eventTypes;
    private final BookingRepository bookings;
    private final Clock clock;

        public GroundService(VenueRepository venues, 
        GroundRepository grounds, EventTypeRepository eventTypes,
        BookingRepository bookings, Clock clock) {
        this.venues = venues;
        this.grounds = grounds;
        this.eventTypes = eventTypes;
        this.bookings = bookings;
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
   
    /**
     * A ground's day - by default today by the clock - with that weekday's hours and its bookings in `from` order,
     * each with its shadow worked out now from its event type.
     */
    public DayView day(String venue, String ground, LocalDate date) {
        long venueId = venueId(venue);
        GroundRepository.Row row = grounds.one(venueId, ground).orElseThrow(() -> new NoSuchGround(venue, ground));
        LocalDate day = date != null ? date : LocalDate.ofInstant(clock.instant(), ZoneOffset.UTC);
        LocalTime[] hours = hours(row, day);
      return new DayView(venue, ground, day, hours[0], hours[1], bookings.onDay(venueId, ground, day).stream()
                .map(b -> {
                    Shadow held = shadow(b.from(), b.to(), b.setupMinutes(), b.teardownMinutes());
                    return new DayBooking(b.bookingId(), b.event(), b.from(), b.to(), held.from(), held.to(),
                            b.state());
                }).toList());
    }
  
    /** The shadow: the slot plus its event type's setup before and teardown after. Never stored. */
    public Shadow shadow(Instant from, Instant to, int setupMinutes, int teardownMinutes) {
        return new Shadow(from.minus(Duration.ofMinutes(setupMinutes)), to.plus(Duration.ofMinutes(teardownMinutes)));
    }
    
    /** Opening hours depend on the weekday: Sunday uses the sunday_* columns. */
    public static LocalTime[] hours(GroundRepository.Row ground, LocalDate day) {
        return day.getDayOfWeek() == DayOfWeek.SUNDAY
                ? new LocalTime[] {ground.sundayOpens(), ground.sundayCloses()}
                : new LocalTime[] {ground.opens(), ground.closes()};
                    }

    private long venueId(String venue) {
        return venues.idOf(venue).orElseThrow(() -> new NoSuchVenue(venue));
    }
}
   