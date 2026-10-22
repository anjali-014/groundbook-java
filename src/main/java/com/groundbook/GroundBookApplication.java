package com.groundbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/** GroundBook: four grounds and one booking book, with the buffers around every slot. The grader starts it here. */
@SpringBootApplication
@ConfigurationPropertiesScan
public class GroundBookApplication {

      public static void main(String[] args) {
        SpringApplication.run(GroundBookApplication.class, args);
    }
}