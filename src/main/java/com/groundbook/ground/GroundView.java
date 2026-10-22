package com.groundbook.ground;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/** One ground and its opening hours (06:00): weekdays and Saturday, and Sunday. */
public record GroundView(String ground, String name, @JsonFormat(pattern = "HH:mm") LocalTime opens,
                         @JsonFormat(pattern = "HH:mm") LocalTime closes,
                         @JsonFormat(pattern = "HH:mm") LocalTime sundayOpens,
                         @JsonFormat(pattern = "HH:mm") LocalTime sundayCloses) {
}