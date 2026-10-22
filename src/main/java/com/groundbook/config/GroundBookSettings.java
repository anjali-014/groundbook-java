package com.groundbook.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * The app's own settings, read once at start-up from application.yml. Spring's own settings - the database
 * URL, the host and the port - stay under spring.datasource and server.
 *
 * @param seedDir the folder that holds grounds.csv and event_types.csv
 */
@ConfigurationProperties
public record GroundBookSettings(@DefaultValue("seed") String seedDir) {
}
