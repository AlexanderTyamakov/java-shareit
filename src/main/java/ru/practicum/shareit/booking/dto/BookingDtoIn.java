package ru.practicum.shareit.booking.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.BookingStatus;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class BookingDtoIn {
    private final Long id;
    @NonNull @FutureOrPresent
    private final LocalDateTime start;
    @NonNull  @FutureOrPresent
    private final LocalDateTime end;
    @NonNull
    private final Long itemId;
}
