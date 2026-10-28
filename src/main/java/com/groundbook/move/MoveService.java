package com.groundbook.move;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.groundbook.booking.BookingRepository;
import com.groundbook.booking.BookingService;
import com.groundbook.booking.EventLog;
import com.groundbook.error.AlreadyStarted;
import com.groundbook.error.Cancelled;
import com.groundbook.ground.Shadow;
import com.groundbook.ground.VenueRepository;

/**
 * Moving a booking: the whole new interval is checked - the slot rules and the clash on both buffers, leaving the
 * booking itself out - and the move is one transaction, so the booking is never in two places or none.
 */
@Service
public class MoveService {

    private final VenueRepository venues;
    private final BookingService bookings;
    private final MoveRepository moves;
    private final EventLog events;
    private final Clock clock;

    public MoveService(VenueRepository venues, BookingService bookings, MoveRepository moves, EventLog events, Clock clock) {

        this.venues = venues;
        this.bookings = bookings;
        this.moves = moves;
        this.events = events;
        this.clock = clock;
            }

    /** A cancelled booking cannot move, nor one whose slot has begun; the state stays as it was. */
    @Transactional
    public MoveResponse move(String venue, String bookingId, Instant from, Instant to) {
        long venueId = venues.idOf(venue).orElseThrow();       // the one venue is filled at start-up
        BookingRepository.Row b = bookings.find(venue, bookingId);
        Instant now = clock.instant().truncatedTo(ChronoUnit.SECONDS);
                if ("cancelled".equals(b.state())) {
            throw new Cancelled(bookingId);
        }
                if (!now.isBefore(b.from())) {
            throw new AlreadyStarted(bookingId);
        }
        Shadow held = bookings.slot(venueId, venue, b.ground(), b.event(), from, to);
                bookings.refuseClashes(venueId, b.ground(), from, held, b.id());
        moves.move(b.id(), from, to, now);
                events.append(venueId, b.id(), "moved", now, Map.of("from", from.toString(), "to", to.toString(),
                "previousFrom", b.from().toString(), "previousTo", b.to().toString()));
        return new MoveResponse(bookingId, from, to, held.from(), held.to(), now);
    }
}