package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository mockItemRequestRepository;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private UserRepository mockUserRepository;
    private ItemRequestService itemRequestService;

    private UserDto userDto = new UserDto(1L, "Alex", "alex@alex.ru");

    private ItemDto itemDto = new ItemDto(1L, "itemName", "itemDescription", true, 1L, null, null, null);
    private ItemRequestDtoIn itemRequestDtoIn = new ItemRequestDtoIn("ItemRequest description");
    private ItemRequestDtoOut itemRequestDto = new ItemRequestDtoOut(1L, "ItemRequest description",
            userDto, LocalDateTime.of(2022, 1, 2, 3, 4, 5), null);

    @BeforeEach
    void beforeEach() {
        itemRequestService = new ItemRequestServiceImpl(mockItemRepository, mockUserRepository, mockItemRequestRepository);
    }

    @Test
    void shouldReturnItemRequestWhenFindById() {
        when(mockUserRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(UserDtoMapper.toNewUser(userDto)));
        when(mockItemRequestRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(ItemRequestDtoMapper.toItemRequest(itemRequestDtoIn, UserDtoMapper.toNewUser(userDto), LocalDateTime.of(2022, 1, 2, 3, 4, 5))));
        ItemRequestDtoOut itemRequestDtoOutReturned = itemRequestService.getItemRequestById(1l, itemRequestDto.getId());
        verify(mockUserRepository, Mockito.times(1))
                .findById(1L);
        assertThat(itemRequestDtoOutReturned.getDescription(), equalTo(itemRequestDtoIn.getDescription()));
    }

    @Test
    void shouldGetExceptionWhenGetItemRequestWithWrongId() {
        when(mockUserRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(UserDtoMapper.toNewUser(userDto)));
        when(mockItemRequestRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());
        final ItemRequestNotFoundException exception = Assertions.assertThrows(
                ItemRequestNotFoundException.class,
                () -> itemRequestService.getItemRequestById(1L, -1L));
        Assertions.assertEquals("Запрос вещи с id = -1 не найден", exception.getMessage());
    }
}