package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class BookingDtoIn {
    @NotNull
    @FutureOrPresent(message = "Дата начала не может быть меньше текущей даты")
    private LocalDateTime start;
    @NotNull
    @FutureOrPresent(message = "Дата начала не может быть меньше текущей даты")
    private LocalDateTime end;
    @NotNull
    private Long itemId;
}
