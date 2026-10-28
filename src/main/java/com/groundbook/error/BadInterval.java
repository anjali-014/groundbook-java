package com.groundbook.error;

import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 400 /errors/bad-interval: a valid body that asks for an impossible slot. When the slot's shadow crosses the ground's
 * hours, `opens` and `closes` say the hours for that weekday.
 */
 public class BadInterval extends DomainException {

    private final LocalTime opens;
    private final LocalTime closes;

    public BadInterval(String detail) {
        this(detail, null, null);
    }

    public BadInterval(String detail, LocalTime opens, LocalTime closes) {
        super(400, "/errors/bad-interval", "Bad interval", detail);
        this.opens = opens;
        this.closes = closes;
    }

    @Override
    public Map<String, Object> properties() {
        Map<String, Object> hours = new LinkedHashMap<>();
                if (opens != null) {
            hours.put("opens", opens.toString());
            hours.put("closes", closes.toString());
        }
                return hours;
    }
}