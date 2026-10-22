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