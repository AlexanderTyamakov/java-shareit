package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;
    private UserDto userDto = new UserDto(12L, "Alex", "alex@alex.ru");

    private ItemRequestDtoOut itemRequestDtoOut = new ItemRequestDtoOut(20L, "ItemRequest description",
            userDto, LocalDateTime.of(2022, 1, 2, 3, 4, 5), null);
    private ItemRequestDtoIn itemRequestDtoIn = new ItemRequestDtoIn(25L, "ItemRequest description",
            LocalDateTime.of(2022, 1, 2, 3, 4, 5));

    @Test
    void saveRequest() throws Exception {
        when(itemRequestService.saveRequest(eq(12L), any(), any(LocalDateTime.class)))
                .thenReturn(itemRequestDtoIn);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 12L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoIn.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoIn.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDtoIn.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void getItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(eq(12L), eq(20L)))
                .thenReturn(itemRequestDtoOut);
        mvc.perform(get("/requests/20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 12L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoOut.getDescription())))
                .andExpect(jsonPath("$.requestor.id", is(itemRequestDtoOut.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.requestor.name", is(itemRequestDtoOut.getRequestor().getName())))
                .andExpect(jsonPath("$.requestor.email", is(itemRequestDtoOut.getRequestor().getEmail())))
                .andExpect(jsonPath("$.created", is(itemRequestDtoOut.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void getItemRequestsOfUser() throws Exception {
        when(itemRequestService.getItemRequestsOfUser(eq(12L)))
                .thenReturn(List.of(itemRequestDtoOut));
        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 12L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDtoOut.getDescription())))
                .andExpect(jsonPath("$.[0].requestor.id", is(itemRequestDtoOut.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.[0].requestor.name", is(itemRequestDtoOut.getRequestor().getName())))
                .andExpect(jsonPath("$.[0].requestor.email", is(itemRequestDtoOut.getRequestor().getEmail())))
                .andExpect(jsonPath("$.[0].created", is(itemRequestDtoOut.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void getAllItemRequests() throws Exception {
        when(itemRequestService.getAllItemRequests(eq(12L), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(itemRequestDtoOut));
        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 12L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDtoOut.getDescription())))
                .andExpect(jsonPath("$.[0].requestor.id", is(itemRequestDtoOut.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.[0].requestor.name", is(itemRequestDtoOut.getRequestor().getName())))
                .andExpect(jsonPath("$.[0].requestor.email", is(itemRequestDtoOut.getRequestor().getEmail())))
                .andExpect(jsonPath("$.[0].created", is(itemRequestDtoOut.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }
}
