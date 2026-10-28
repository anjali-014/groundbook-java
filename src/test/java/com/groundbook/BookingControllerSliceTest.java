package com.groundbook;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.groundbook.booking.BookingController;
import com.groundbook.booking.BookingService;
import com.groundbook.error.AlreadyStarted;
import com.groundbook.error.Cancelled;

/**
 * Only the web layer - the booking controller and the one exception handler - with the service replaced by a
 * mock. It proves each refusal the service throws is mapped to its status and type, without a database.
 */
 @WebMvcTest(BookingController.class)
class BookingControllerSliceTest {

    @Autowired
    MockMvc http;

        @MockitoBean
    BookingService bookings;

    @Test
    void cancelledIsMappedTo409() throws Exception {
                doThrow(new Cancelled("BK0412")).when(bookings).confirm("deccan", "BK0412");
                        http.perform(post("/bookings/BK0412/confirm"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("/errors/cancelled"));
    }

        @Test
    void alreadyStartedIsMappedTo409WithItsOwnType() throws Exception {
        doThrow(new AlreadyStarted("BK0413")).when(bookings).confirm("deccan", "BK0413");
                http.perform(post("/bookings/BK0413/confirm"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("/errors/already-started"));
    }
}