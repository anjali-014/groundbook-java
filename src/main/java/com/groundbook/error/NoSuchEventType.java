package com.groundbook.error;

/** 400 /errors/no-such-event-type. */
public class NoSuchEventType extends DomainException {
  
      public NoSuchEventType(String event) {
        super(400, "/errors/no-such-event-type", "No such event type", "There is no event type " + event);
    }
}