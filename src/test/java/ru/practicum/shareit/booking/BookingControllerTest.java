package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookerDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoShort;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private BookingDtoIn bookingDtoIn = new BookingDtoIn(
            LocalDateTime.of(2025, 12, 25, 12, 00, 00),
            LocalDateTime.of(2025, 12, 26, 12, 00, 00),
            2L);

    private BookingDtoOut bookingDtoOut = new BookingDtoOut(
            5L,
            LocalDateTime.of(2025, 12, 25, 12, 00, 00),
            LocalDateTime.of(2025, 12, 26, 12, 00, 00),
            new ItemDtoShort(7L, "Item"),
            new BookerDto(9L),
            BookingStatus.WAITING);

    @Test
    void saveBooking() throws Exception {
        when(bookingService.saveBooking(eq(9L), any()))
                .thenReturn(bookingDtoOut);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 9))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(bookingDtoOut.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(bookingDtoOut.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(eq(10L), eq(5L)))
                .thenReturn(bookingDtoOut);

        mvc.perform(get("/bookings/5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 10))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(bookingDtoOut.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(bookingDtoOut.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString()), BookingStatus.class));
    }


    @Test
    void getBookingsOfUser() throws Exception {
        when(bookingService.getBookingsOfUser(eq(9L), nullable(String.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(bookingDtoOut));
        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 9))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(bookingDtoOut.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end", is(bookingDtoOut.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].item.id", is(bookingDtoOut.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDtoOut.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(bookingDtoOut.getStatus().toString())));
    }

    @Test
    void getBookingsOfOwner() throws Exception {
        when(bookingService.getBookingsOfOwner(eq(10L), nullable(String.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(bookingDtoOut));

        mvc.perform(get("/bookings/owner?from=0&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 10))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(bookingDtoOut.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end", is(bookingDtoOut.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].item.id", is(bookingDtoOut.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDtoOut.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(bookingDtoOut.getStatus().toString())));
    }

    @Test
    void changeStatus() throws Exception {
        when(bookingService.changeStatus(eq(2L), eq(5L), any(Boolean.class)))
                .thenReturn(bookingDtoOut);
        mvc.perform(patch("/bookings/5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .queryParam("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(bookingDtoOut.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(bookingDtoOut.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())));
    }
}