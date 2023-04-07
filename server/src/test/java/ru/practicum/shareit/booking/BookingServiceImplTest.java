package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    private BookingRepository mockBookingRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private ItemRepository mockItemRequestRepository;

    private UserDto userDto = new UserDto(1L, "Ivan", "ivan@ivan.ru");


    @Test
    void shouldGetExceptionWhenGetBookingWithWrongId() {
        BookingService bookingService = new BookingServiceImpl(mockItemRequestRepository, mockUserRepository, mockBookingRepository);
        when(mockBookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());
        when(mockUserRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(UserDtoMapper.toNewUser(userDto)));
        final BookingNotFoundException exception = Assertions.assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.getBookingById(-1L, 1L));
        Assertions.assertEquals("Бронирование с id = 1 не найдено", exception.getMessage());
    }
}