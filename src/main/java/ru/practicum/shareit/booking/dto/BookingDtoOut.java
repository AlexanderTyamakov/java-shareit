package ru.practicum.shareit.booking.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoShort;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class BookingDtoOut {
    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final ItemDtoShort item;
    private final BookerDto booker;
    private final BookingStatus status;
}
