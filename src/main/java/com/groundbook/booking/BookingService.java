package com.groundbook.booking;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.groundbook.config.GroundBookSettings;
import com.groundbook.error.AlreadyCancelled;
import com.groundbook.error.AlreadyConfirmed;
import com.groundbook.error.AlreadyOver;
import com.groundbook.error.AlreadyStarted;
import com.groundbook.error.BadInterval;
import com.groundbook.error.Cancelled;
import com.groundbook.error.InvalidRequest;
import com.groundbook.error.NoSuchBooking;
import com.groundbook.error.NoSuchEventType;
import com.groundbook.error.NoSuchGround;
import com.groundbook.ground.EventTypeRepository;
import com.groundbook.ground.GroundRepository;
import com.groundbook.ground.GroundService;
import com.groundbook.ground.Shadow;
import com.groundbook.ground.VenueRepository;

/**
 * Requests for slots, their transitions (confirm, cancel) and reading a booking back. Every `now` comes from the
 * injected clock; all times are UTC.
 */
@Service
public class BookingService {

    private final VenueRepository venues;
    private final GroundRepository grounds;
    private final EventTypeRepository eventTypes;
    private final GroundService groundService;
    private final BookingRepository bookings;
    private final GroundBookSettings settings;
    private final Clock clock;

    public BookingService(VenueRepository venues, GroundRepository grounds, EventTypeRepository eventTypes,
                              GroundService groundService, BookingRepository bookings, GroundBookSettings settings,
                              Clock clock) {
        this.venues = venues;
        this.grounds = grounds;
        this.eventTypes = eventTypes;
        this.groundService = groundService;
        this.bookings = bookings;
        this.settings = settings;
        this.clock = clock;
    }

    /** A request is held for HOLD_HOURS from the moment it is made; no overlap check yet. */
    public RequestedBooking request(String venue, String ground, String event, Instant from, Instant to, String club) {
        long venueId = venues.idOf(venue).orElseThrow();       // the one venue is seeded at start-up
        Shadow held = slot(venueId, venue, ground, event, from, to);
        Instant now = now();
        Instant holdUntil = now.plus(Duration.ofHours(settings.holdHours()));
        long id = bookings.insert(venueId, ground, event, club, from, to, now, holdUntil);
                return new RequestedBooking(BookingIds.format(id), ground, event, from, to, held.from(), held.to(),
                "requested", holdUntil);
    }

    /** Confirm needs a requested booking whose slot has not begun. */
    public ConfirmResponse confirm(String venue, String bookingId) {
        BookingRepository.Row b = find(venue, bookingId);
        Instant now = now();
            switch (b.state()) {
            case "cancelled" -> throw new Cancelled(bookingId);
            case "confirmed" -> throw new AlreadyConfirmed(bookingId);
                        default -> {
                if (!now.isBefore(b.from())) {
                    throw new AlreadyStarted(bookingId);
                }
                            }
        }
        bookings.confirm(b.id(), now);
        return new ConfirmResponse(bookingId, "confirmed", now);
    }

        /**
     * Cancel needs a requested or confirmed booking whose slot has not ended; once the slot has begun the cancel is
     * late - allowed, and flagged. Nobody can book the past, so the rest of the slot needs no
     * protecting.
     */
         public CancelResponse cancel(String venue, String bookingId) {
        BookingRepository.Row b = find(venue, bookingId);
        Instant now = now();
             if ("cancelled".equals(b.state())) {
            throw new AlreadyCancelled(bookingId);
        }
                if (!now.isBefore(b.to())) {
            throw new AlreadyOver(bookingId);
        }
        boolean late = !now.isBefore(b.from());
                bookings.cancel(b.id(), now, late);
        return new CancelResponse(bookingId, "cancelled", now, late);
    }
    public BookingView booking(String venue, String bookingId) {
            BookingRepository.Row b = find(venue, bookingId);
                        Shadow held = groundService.shadow(b.from(), b.to(), b.setupMinutes(), b.teardownMinutes());
        return new BookingView(bookingId, b.ground(), b.event(), b.from(), b.to(), held.from(), held.to(), b.state());
    }

    private BookingRepository.Row find(String venue, String bookingId) {
        long venueId = venues.idOf(venue).orElseThrow();
                return BookingIds.parse(bookingId).flatMap(id -> bookings.find(venueId, id))
                .orElseThrow(() -> new NoSuchBooking(bookingId));
    }

    private Instant now() {
        return clock.instant().truncatedTo(ChronoUnit.SECONDS);
    }
    /**
     * Checks a slot in order - the ground, the event type, `to` after `from`, 30 minutes to 12 hours, not in the past,
     * one date, and the shadow inside that weekday's hours - and answers its shadow.
     */
         private Shadow slot(long venueId, String venue, String ground, String event, Instant from, Instant to) {
        GroundRepository.Row g = grounds.one(venueId, ground).orElseThrow(() -> new NoSuchGround(venue, ground));
        EventTypeRepository.Row type = eventTypes.one(venueId, event).orElseThrow(() -> new NoSuchEventType(event));
                if (!to.isAfter(from)) {
            throw new BadInterval("The slot ends at " + to + ", not after it starts at " + from);
        }
                Duration length = Duration.between(from, to);
        if (length.compareTo(Duration.ofMinutes(30)) < 0 || length.compareTo(Duration.ofHours(12)) > 0) {
            throw new InvalidRequest("to", "a slot is 30 minutes to 12 hours long");
        }
                if (from.isBefore(clock.instant())) {
            throw new BadInterval("The slot starts at " + from + ", in the past");
        }
                LocalDate day = LocalDate.ofInstant(from, ZoneOffset.UTC);
        if (!day.equals(LocalDate.ofInstant(to, ZoneOffset.UTC))) {
            throw new BadInterval("The slot runs past midnight; both ends must be on the same date");
        }
                Shadow held = groundService.shadow(from, to, type.setupMinutes(), type.teardownMinutes());
        LocalTime[] hours = GroundService.hours(g, day);
                if (held.from().isBefore(day.atTime(hours[0]).toInstant(ZoneOffset.UTC))
                || held.to().isAfter(day.atTime(hours[1]).toInstant(ZoneOffset.UTC))) {
            throw new BadInterval("Ground " + ground + " would be held " + held.from() + " to " + held.to()
                    + "; it opens at " + hours[0] + " and closes at " + hours[1], hours[0], hours[1]);
        }
                return held;
    }
}