package com.groundbook.booking;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** A booking's public id - BK0412 - made from its database id, which is never shown itself. */
public final class BookingIds {

    private static final Pattern PUBLIC = Pattern.compile("BK(\\d+)");

    private BookingIds() {
    }

    public static String format(long id) {
        return String.format("BK%04d", id);
    }

    /** The database id behind a public id, or empty when it is not one. */
    public static Optional<Long> parse(String publicId) {
        Matcher m = PUBLIC.matcher(publicId);
                return m.matches() ? Optional.of(Long.parseLong(m.group(1))) : Optional.empty();
    }
}