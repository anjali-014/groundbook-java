package com.groundbook;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import com.groundbook.booking.BookingService;

/** The API docs a club's developer builds against, and the one shape every error has - a 500 too. */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:api-docs-test")
@AutoConfigureMockMvc
class ApiDocsTest {

         @Autowired
    MockMvc http;

    @MockitoSpyBean
    BookingService bookings;

    @Test
    void theSpecDocumentsEveryEndpointAndItsRefusals() throws Exception {
        http.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/bookings'].post.summary").isNotEmpty())
                .andExpect(jsonPath("$.paths['/bookings'].post.responses['409']").exists())
                .andExpect(jsonPath("$.paths['/bookings/{bookingId}/move'].post.tags[0]").value("moves"));
    }

        @Test
    void anUnexpectedErrorIsAGeneric500WithARequestId() throws Exception {
        doThrow(new IllegalStateException("the scorer's hut is on fire")).when(bookings)
                .booking(anyString(), anyString());
        http.perform(get("/bookings/BK0001"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.type").value("/errors/internal"))
                .andExpect(jsonPath("$.requestId").isNotEmpty())
                                .andExpect(content().string(Matchers.not(Matchers.containsString("on fire"))));
    }
    }
    