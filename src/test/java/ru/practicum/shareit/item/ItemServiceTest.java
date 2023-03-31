package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private UserDto userDto1 = new UserDto(101L, "Alex", "alex@alex.ru");
    private UserDto userDto2 = new UserDto(102L, "Egor", "egor@egor.ru");
    private ItemDto itemDto = new ItemDto(100L, "Item1", "Description1", true,
            null, null, null, null);
    private ItemDto itemDto2 = new ItemDto(102L, "Item2", "Description2", true,
            null, null, null, null);

    @Test
    void shouldSaveItem() {
        UserDto newUserDto = userService.saveUser(userDto1);
        ItemDto newItemDto = itemService.saveItem(newUserDto.getId(), itemDto);
        ItemDto returnItemDto = itemService.getItemById(newUserDto.getId(), newItemDto.getId());
        assertThat(returnItemDto.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void shouldUpdateItem() {
        UserDto newUserDto = userService.saveUser(userDto1);
        ItemDto newItemDto = itemService.saveItem(newUserDto.getId(), itemDto);
        newItemDto.setName("NewName");
        newItemDto.setDescription("NewDescription");
        newItemDto.setAvailable(false);
        ItemDto returnItemDto = itemService.updateItem(newUserDto.getId(), newItemDto, newItemDto.getId());
        assertThat(returnItemDto.getName(), equalTo("NewName"));
        assertThat(returnItemDto.getDescription(), equalTo("NewDescription"));
        assertFalse(returnItemDto.getAvailable());
    }

    @Test
    void shouldGetExceptionWhenUpdateItemNotOwner() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(), itemDto);
        UserNotFoundException exp = assertThrows(UserNotFoundException.class,
                () -> itemService.updateItem(newUserDto.getId(), newItemDto, newItemDto.getId()));
        assertEquals("Пользователь с id = " + newUserDto.getId()
                + " не является владельцем вещи с id = " + newItemDto.getId(), exp.getMessage());
    }

    @Test
    void shouldReturnItemsByOwner() {
        UserDto ownerDto = userService.saveUser(userDto1);
        itemService.saveItem(ownerDto.getId(), itemDto);
        itemService.saveItem(ownerDto.getId(), itemDto2);
        List<ItemDto> listItems = itemService.getAllItemsOfUser(ownerDto.getId(), 0, 10);
        assertEquals(2, listItems.size());
    }

    @Test
    void shouldReturnItemsBySearchSizeIsNotNull() {
        UserDto ownerDto1 = userService.saveUser(userDto1);
        UserDto ownerDto2 = userService.saveUser(userDto2);
        itemService.saveItem(ownerDto1.getId(), itemDto);
        itemService.saveItem(ownerDto2.getId(), itemDto2);
        List<ItemDto> listItems = itemService.searchItem(ownerDto2.getId(), "item", 0, 1);
        assertEquals(1, listItems.size());
    }

    @Test
    void shouldReturnItemsBySearchWhenSizeIsNull() {
        UserDto ownerDto = userService.saveUser(userDto1);
        itemService.saveItem(ownerDto.getId(), itemDto);
        itemService.saveItem(ownerDto.getId(), itemDto2);
        List<ItemDto> listItems = itemService.searchItem(ownerDto.getId(), "item", 0, null);
        assertEquals(2, listItems.size());
    }

    @Test
    void searchItemWithBlankText() {
        UserDto ownerDto = userService.saveUser(userDto1);
        itemService.saveItem(ownerDto.getId(), itemDto);
        itemService.saveItem(ownerDto.getId(), itemDto2);
        List<ItemDto> itemList = itemService.searchItem(ownerDto.getId(), "", 0, 5);
        assertTrue(itemList.isEmpty());
    }

    @Test
    void shouldGetExceptionWhenAddCommentWhenUserNotBooker() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(), itemDto);
        System.out.println(newItemDto);
        CommentDtoIn commentDtoIn = new CommentDtoIn("Comment1");
        ValidationException exp = assertThrows(ValidationException.class,
                () -> itemService.addComment(newUserDto.getId(), commentDtoIn, newItemDto.getId()));
        assertEquals("Бронирование пользователем id = " + newUserDto.getId()
                + " вещи id = " + newItemDto.getId() + " не найдено", exp.getMessage());
    }

    @Test
    void shouldAddComment() {
        UserDto ownerDto = userService.saveUser(userDto1);
        UserDto newUserDto = userService.saveUser(userDto2);
        ItemDto newItemDto = itemService.saveItem(ownerDto.getId(), itemDto);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(3),
                newItemDto.getId()
        );
        BookingDtoOut bookingDto = bookingService.saveBooking(newUserDto.getId(), bookingDtoIn);
        bookingService.changeStatus(ownerDto.getId(), bookingDto.getId(), true);
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        CommentDtoIn commentDtoIn = new CommentDtoIn("Comment1");
        itemService.addComment(newUserDto.getId(), commentDtoIn, newItemDto.getId());
        Assertions.assertEquals(1, itemService.getItemById(ownerDto.getId(), newItemDto.getId()).getComments().size());
    }
}
