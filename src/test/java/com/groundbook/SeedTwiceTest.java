package com.groundbook;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Start-up seeds once - the app started twice on the same database still has 4 grounds, not 8. */
class SeedTwiceTest {

    @TempDir
    Path dir;

        private static ConfigurableApplicationContext start(String databaseUrl) {
        return new SpringApplicationBuilder(GroundBookApplication.class)
                .properties("spring.datasource.url=" + databaseUrl, "server.port=0")
                .run();
    }
    
        private static JsonNode grounds(ConfigurableApplicationContext app) throws Exception {
        String port = app.getEnvironment().getProperty("local.server.port");
                HttpResponse<String> r = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/venues/deccan/grounds"))
                        .build(),
                HttpResponse.BodyHandlers.ofString());
                        assertThat(r.statusCode()).isEqualTo(200);
        return new ObjectMapper().readTree(r.body());
    }

        @Test
    void aSecondStartLoadsNothingAgain() throws Exception {
        String url = "jdbc:h2:file:" + dir.resolve("groundbook").toString().replace('\\', '/');
                try (ConfigurableApplicationContext first = start(url)) {
            assertThat(grounds(first).path("grounds")).hasSize(4);
        }
                try (ConfigurableApplicationContext second = start(url)) {
            JsonNode grounds = grounds(second);
            assertThat(grounds.path("grounds")).hasSize(4);
        }
            }
}