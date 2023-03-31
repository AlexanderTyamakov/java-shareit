package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    private final ItemRequestService itemRequestService;

    private final UserService userService;
    private UserDto userDto1 = new UserDto(101L, "Ivan", "ivan@ivan.ru");
    private UserDto userDto2 = new UserDto(102L, "Egor", "egor@egor.ru");

    private ItemRequestDtoIn itemRequestDtoIn = new ItemRequestDtoIn("ItemRequest description");
    private ItemRequestDtoOut itemRequestDto = new ItemRequestDtoOut(100L, "ItemRequest description",
            userDto1, LocalDateTime.of(2022, 1, 2, 3, 4, 5), null);

    @Test
    void shouldSaveItemRequest() {
        UserDto newUserDto = userService.saveUser(userDto1);
        ItemRequestDtoOut returnRequestDtoOut = itemRequestService.saveRequest(newUserDto.getId(), itemRequestDtoIn,
                LocalDateTime.of(2022, 1, 2, 3, 4, 5));
        assertThat(returnRequestDtoOut.getDescription(), equalTo(itemRequestDto.getDescription()));
    }

    @Test
    void shouldGetExceptionWhenSaveItemRequestWithWrongUserId() {
        UserNotFoundException exp = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.saveRequest(-5L, itemRequestDtoIn,
                        LocalDateTime.of(2022, 1, 2, 3, 4, 5)));
        assertEquals("Пользователь с id = -5 не найден", exp.getMessage());
    }

    @Test
    void shouldGetExceptionWhenGetItemRequestWithWrongId() {
        UserDto firstUserDto = userService.saveUser(userDto1);
        ItemRequestNotFoundException exp = assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getItemRequestById(firstUserDto.getId(), -2L));
        assertEquals("Запрос вещи с id = -2 не найден", exp.getMessage());
    }

    @Test
    void shouldReturnAllItemRequestsWhenSizeNotNull() {
        UserDto firstUserDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        itemRequestService.saveRequest(newUserDto.getId(), itemRequestDtoIn,
                LocalDateTime.of(2023, 1, 2, 3, 4, 5));
        itemRequestService.saveRequest(newUserDto.getId(), itemRequestDtoIn,
                LocalDateTime.of(2024, 1, 2, 3, 4, 5));
        List<ItemRequestDtoOut> listItemRequest = itemRequestService.getAllItemRequests(firstUserDto.getId(),
                0, 10);
        assertThat(listItemRequest.size(), equalTo(2));
    }

    @Test
    void shouldReturnAllItemRequestsWhenSizeNull() {
        UserDto firstUserDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        itemRequestService.saveRequest(newUserDto.getId(), itemRequestDtoIn,
                LocalDateTime.of(2023, 1, 2, 3, 4, 5));
        itemRequestService.saveRequest(newUserDto.getId(), itemRequestDtoIn,
                LocalDateTime.of(2023, 6, 6, 6, 6, 6));
        List<ItemRequestDtoOut> listItemRequest = itemRequestService.getAllItemRequests(firstUserDto.getId(),
                0, null);
        assertThat(listItemRequest.size(), equalTo(2));
    }

    @Test
    void shouldReturnItemRequestsOfUser() {
        userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        itemRequestService.saveRequest(newUserDto.getId(), itemRequestDtoIn,
                LocalDateTime.of(2023, 1, 2, 3, 4, 5));
        itemRequestService.saveRequest(newUserDto.getId(), itemRequestDtoIn,
                LocalDateTime.of(2024, 1, 2, 3, 4, 5));
        List<ItemRequestDtoOut> listItemRequest = itemRequestService.getItemRequestsOfUser(newUserDto.getId());
        System.out.println(listItemRequest.toString());
        assertThat(listItemRequest.size(), equalTo(2));
    }

    @Test
    void shouldReturnItemRequestById() {
        UserDto firstUserDto = userService.saveUser(userDto1);
        ItemRequestDtoOut newItemRequestDto = itemRequestService.saveRequest(firstUserDto.getId(), itemRequestDtoIn,
                LocalDateTime.of(2023, 1, 2, 3, 4, 5));
        ItemRequestDtoOut returnItemRequestDto = itemRequestService.getItemRequestById(newItemRequestDto.getId(),
                firstUserDto.getId());
        assertThat(returnItemRequestDto.getDescription(), equalTo(itemRequestDto.getDescription()));
    }
}