package com.groundbook.ground;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

/**
 * Seeds the venue once every bean exists and before Tomcat starts listening, so no request can see a half-loaded
 * venue. (An ApplicationRunner would run after the server is already accepting requests.)
 */
 @Component
public class GroundSeeder implements SmartInitializingSingleton {

    private final SeedService seed;

        public GroundSeeder(SeedService seed) {
        this.seed = seed;
    }

        @Override
    public void afterSingletonsInstantiated() {
        seed.seed();
    }
}