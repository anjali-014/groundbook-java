package com.groundbook.error;

import java.util.List;
import java.util.Map;

/**
 * 400 /errors/validation for a rule the service checks because it spans two fields (a slot shorter than 30 minutes or
 * longer than 12 hours): the same shape as a body that fails Bean Validation, with `errors[]` naming the field.
 */
 public class InvalidRequest extends DomainException {

    private final String field;
    private final String reason;

        public InvalidRequest(String field, String reason) {
        super(400, "/errors/validation", "Invalid request", "The request is not valid");
        this.field = field;
        this.reason = reason;
    }

        @Override
    public Map<String, Object> properties() {
        return Map.of("errors", List.of(Map.of("field", field, "message", reason)));
    }
}

