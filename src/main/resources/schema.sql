-- The venue, its grounds and its event types. Run at every start-up, so every table is created only if missing.
-- A booking's shadow is not stored: it is derived on read from its event type's buffers.

CREATE TABLE IF NOT EXISTS venues (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    slug VARCHAR(40) NOT NULL UNIQUE,
    name VARCHAR(80) NOT NULL
);

CREATE TABLE IF NOT EXISTS grounds (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    venue_id      BIGINT      NOT NULL REFERENCES venues (id),
    code          VARCHAR(4)  NOT NULL,
    name          VARCHAR(40) NOT NULL,
    opens         TIME        NOT NULL,
    closes        TIME        NOT NULL,
    sunday_opens  TIME        NOT NULL,
    sunday_closes TIME        NOT NULL,
    UNIQUE (venue_id, code)
);

CREATE TABLE IF NOT EXISTS event_types (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    venue_id         BIGINT      NOT NULL REFERENCES venues (id),
    code             VARCHAR(20) NOT NULL,
    name             VARCHAR(40) NOT NULL,
    setup_minutes    INT         NOT NULL,
    teardown_minutes INT         NOT NULL,
    UNIQUE (venue_id, code)
);

-- A club's request for a slot. Its shadow is not stored: it is worked out on read from the event type. The club's
-- name is kept and never sent back. It also keeps confirmed_at, cancelled_at, and late_cancel when it was cancelled after its
-- slot began.
CREATE TABLE IF NOT EXISTS bookings (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    venue_id      BIGINT                   NOT NULL REFERENCES venues (id),
    ground_id     BIGINT                   NOT NULL REFERENCES grounds (id),
    event_type_id BIGINT                   NOT NULL REFERENCES event_types (id),
    club_name     VARCHAR(80)              NOT NULL,
    from_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    to_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    state         VARCHAR(10)              NOT NULL,
    requested_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    hold_until    TIMESTAMP WITH TIME ZONE NOT NULL,
    confirmed_at  TIMESTAMP WITH TIME ZONE,
    cancelled_at  TIMESTAMP WITH TIME ZONE,
    late_cancel   BOOLEAN                  NOT NULL DEFAULT FALSE
);