package com.groundbook.error;

import java.util.List;
import java.util.Map;

public class Overlap extends DomainException {
    private final List<Map<String, Object>> errors;

    public Overlap(List<Map<String, Object>> errors) {
        super(409, "/errors/overlap", "Overlap", "The ground is already held");
        this.errors = errors;
    }

    @Override
    public Map<String, Object> properties() {
        return Map.of("errors", errors);
    }
}
