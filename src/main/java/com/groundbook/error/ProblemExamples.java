package com.groundbook.error;

/** The example error bodies the API docs show for each refusal - the exact shape a client will receive. */
public final class ProblemExamples {

public static final String PROBLEM = "application/problem+json";

    public static final String VALIDATION_DATE = """
            {"type":"/errors/validation","title":"Invalid request","status":400,"detail":"The request is not valid",\
            "instance":"/venues/deccan/grounds/A/day","errors":[{"field":"date",\
            "message":"'saturday' is not an allowed value"}]}""";
                public static final String VALIDATION_REQUEST = """
            {"type":"/errors/validation","title":"Invalid request","status":400,"detail":"The request is not valid",\
            "instance":"/bookings","errors":[{"field":"from","message":"must not be null"}]}""";
            public static final String VALIDATION_MOVE = """
            {"type":"/errors/validation","title":"Invalid request","status":400,"detail":"The request is not valid",\
            "instance":"/bookings/BK0412/move","errors":[{"field":"to","message":"must not be null"}]}""";
            public static final String NO_SUCH_VENUE = """
            {"type":"/errors/no-such-venue","title":"No such venue","status":404,\
            "detail":"There is no venue mumbai","instance":"/venues/mumbai/grounds"}""";
            public static final String NO_SUCH_GROUND = """
            {"type":"/errors/no-such-ground","title":"No such ground","status":404,\
            "detail":"There is no ground Z at deccan","instance":"/venues/deccan/grounds/Z/day"}""";
            public static final String NO_SUCH_GROUND_REQUEST = """
            {"type":"/errors/no-such-ground","title":"No such ground","status":404,\
            "detail":"There is no ground Z at deccan","instance":"/bookings"}""";
             public static final String NO_SUCH_EVENT_TYPE = """
            {"type":"/errors/no-such-event-type","title":"No such event type","status":400,\
            "detail":"There is no event type concert","instance":"/bookings"}""";
            public static final String BAD_INTERVAL = """
            {"type":"/errors/bad-interval","title":"Bad interval","status":400,\
            "detail":"Ground A would be held 2026-10-03T05:40:00Z to 2026-10-03T08:30:00Z; \
            it opens at 06:00 and closes at 22:00",\
            "instance":"/bookings","opens":"06:00","closes":"22:00"}""";
            public static final String BAD_INTERVAL_MOVE = """
            {"type":"/errors/bad-interval","title":"Bad interval","status":400,\
            "detail":"The slot ends at 2026-10-03T13:00:00Z, not after it starts at 2026-10-03T16:00:00Z",\
            "instance":"/bookings/BK0412/move"}""";
            public static final String OVERLAP = """
            {"type":"/errors/overlap","title":"Overlap","status":409,\
            "detail":"With its buffers this slot clashes with 1 booking(s) on the ground","instance":"/bookings",\
            "clashesWith":[{"bookingId":"BK0411","groundHeldFrom":"2026-10-03T13:20:00Z",\
            "groundHeldTo":"2026-10-03T16:30:00Z"}]}""";
            public static final String OVERLAP_CONFIRM = """
            {"type":"/errors/overlap","title":"Overlap","status":409,\
            "detail":"With its buffers this slot clashes with 1 booking(s) on the ground",\
            "instance":"/bookings/BK0412/confirm","clashesWith":[{"bookingId":"BK0411",\
            "groundHeldFrom":"2026-10-03T13:20:00Z","groundHeldTo":"2026-10-03T16:30:00Z"}]}""";
            public static final String OVERLAP_MOVE = """
            {"type":"/errors/overlap","title":"Overlap","status":409,\
            "detail":"With its buffers this slot clashes with 1 booking(s) on the ground",\
            "instance":"/bookings/BK0412/move","clashesWith":[{"bookingId":"BK0411",\
            "groundHeldFrom":"2026-10-03T13:20:00Z","groundHeldTo":"2026-10-03T16:30:00Z"}]}""";
            public static final String NO_SUCH_BOOKING = """
            {"type":"/errors/no-such-booking","title":"No such booking","status":404,\
            "detail":"There is no booking BK9999","instance":"/bookings/BK9999"}""";
            public static final String NO_SUCH_BOOKING_CONFIRM = """
            {"type":"/errors/no-such-booking","title":"No such booking","status":404,\
            "detail":"There is no booking BK9999","instance":"/bookings/BK9999/confirm"}""";
            public static final String NO_SUCH_BOOKING_CANCEL = """
            {"type":"/errors/no-such-booking","title":"No such booking","status":404,\
            "detail":"There is no booking BK9999","instance":"/bookings/BK9999/cancel"}""";
            public static final String NO_SUCH_BOOKING_MOVE = """
            {"type":"/errors/no-such-booking","title":"No such booking","status":404,\
            "detail":"There is no booking BK9999","instance":"/bookings/BK9999/move"}""";
            public static final String ALREADY_STARTED = """
            {"type":"/errors/already-started","title":"Already started","status":409,\
            "detail":"Booking BK0412 has begun; it can no longer be confirmed or moved",\
            "instance":"/bookings/BK0412/confirm"}""";
            public static final String ALREADY_STARTED_MOVE = """
            {"type":"/errors/already-started","title":"Already started","status":409,\
            "detail":"Booking BK0412 has begun; it can no longer be confirmed or moved",\
            "instance":"/bookings/BK0412/move"}""";
            public static final String CANCELLED = """
            {"type":"/errors/cancelled","title":"Cancelled","status":409,\
            "detail":"Booking BK0412 is cancelled; request a new one","instance":"/bookings/BK0412/confirm"}""";
            public static final String CANCELLED_MOVE = """
            {"type":"/errors/cancelled","title":"Cancelled","status":409,\
            "detail":"Booking BK0412 is cancelled; request a new one","instance":"/bookings/BK0412/move"}""";
            public static final String ALREADY_CONFIRMED = """
            {"type":"/errors/already-confirmed","title":"Already confirmed","status":409,\
            "detail":"Booking BK0412 is already confirmed","instance":"/bookings/BK0412/confirm"}""";
            public static final String ALREADY_OVER = """
            {"type":"/errors/already-over","title":"Already over","status":409,\
            "detail":"Booking BK0412 has ended; it can no longer be cancelled","instance":"/bookings/BK0412/cancel"}""";
            public static final String ALREADY_CANCELLED = """
            {"type":"/errors/already-cancelled","title":"Already cancelled","status":409,\
            "detail":"Booking BK0412 is already cancelled","instance":"/bookings/BK0412/cancel"}""";
            
    private ProblemExamples() {
    }
}