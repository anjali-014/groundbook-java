package com.groundbook.booking;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import com.groundbook.error.Overlap;
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


     /**
     * A request is held for HOLD_HOURS from the moment it is made; its shadow must clash with no live booking on the
     * ground.
     */
    public RequestedBooking request(String venue, String ground, String event, Instant from, Instant to, String club) {
        long venueId = venues.idOf(venue).orElseThrow();      
        Shadow held = slot(venueId, venue, ground, event, from, to);
        refuseClashes(venueId, ground, from, held, 0); 
        Instant now = now();
        Instant holdUntil = now.plus(Duration.ofHours(settings.holdHours()));
        long id = bookings.insert(venueId, ground, event, club, from, to, now, holdUntil);
        return new RequestedBooking(BookingIds.format(id), ground, event, from, to, held.from(), held.to(),
                "requested", holdUntil);
    }

       /**
     * Confirm needs a requested booking whose slot has not begun, and re-runs the clash check (a competing request may
     * have been confirmed since).
     */
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
        Shadow held = groundService.shadow(b.from(), b.to(), b.setupMinutes(), b.teardownMinutes());
        long venueId = venues.idOf(venue).orElseThrow();
        refuseClashes(venueId, b.ground(), b.from(), held, b.id());
        
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
        Shadow held = shadowOf(b);
        return new BookingView(bookingId, b.ground(), b.event(), b.from(), b.to(), held.from(), held.to(), b.state());
    }
    
    /**
     * A shadow may not clash with the shadow of any live booking on the same ground and date; the booking itself
     * (id `self`, 0 for a new one) is left out.
     */
    private void refuseClashes(long venueId, String ground, Instant from, Shadow held, long self) {
        Instant now = clock.instant();
        List<Map<String, Object>> clashes = bookings.onDay(venueId, ground, LocalDate.ofInstant(from, ZoneOffset.UTC))
                .stream()
                .filter(other -> other.id() != self && other.live(now) && held.clashes(shadowOf(other)))
                .map(other -> {
                    Shadow theirs = shadowOf(other);
                    Map<String, Object> clash = new LinkedHashMap<>();
                    clash.put("bookingId", other.bookingId());
                    clash.put("groundHeldFrom", theirs.from().toString());
                    clash.put("groundHeldTo", theirs.to().toString());
                    return clash;
                }).toList();
        if (!clashes.isEmpty()) {
            throw new Overlap(clashes);
        }
    }

    private Shadow shadowOf(BookingRepository.Row b) {
        return groundService.shadow(b.from(), b.to(), b.setupMinutes(), b.teardownMinutes());
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
        var g = grounds.one(venueId, ground)
                .orElseThrow(() -> new NoSuchGround(venue, ground));
        
        var t = eventTypes.all(venueId).stream()
                .filter(type -> type.code().equals(event))
                .findFirst()
                .orElseThrow(() -> new NoSuchEventType(event));

        if (!to.isAfter(from)) {
            throw new BadInterval("to must be after from");
        }

        Duration duration = Duration.between(from, to);
        if (duration.toMinutes() < 30 || duration.toMinutes() > 720) {
            throw new BadInterval("Duration must be between 30 minutes and 12 hours");
        }

        Instant now = now();
        if (from.isBefore(now)) {
            throw new BadInterval("Cannot book in the past");
        }

        LocalDate dateFrom = LocalDate.ofInstant(from, ZoneOffset.UTC);
        LocalDate dateTo = LocalDate.ofInstant(to, ZoneOffset.UTC);
        if (!dateFrom.equals(dateTo)) {
            throw new BadInterval("Booking must wrap within a single day");
        }

        Shadow shadow = groundService.shadow(from, to, t.setupMinutes(), t.teardownMinutes());
        LocalTime[] hours = GroundService.hours(g, dateFrom);
        
        LocalTime shadowStart = LocalTime.ofInstant(shadow.from(), ZoneOffset.UTC);
        LocalTime shadowEnd = LocalTime.ofInstant(shadow.to(), ZoneOffset.UTC);

    
         if (shadowStart.isBefore(hours[0]) || shadowEnd.isAfter(hours[1])) {
            throw new BadInterval("Shadow falls outside ground opening hours", hours[0], hours[1]);
        }

        
        return shadow;
    }
}
