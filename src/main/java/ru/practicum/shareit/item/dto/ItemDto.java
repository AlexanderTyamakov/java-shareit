package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.dto.LastBookingDto;
import ru.practicum.shareit.booking.dto.NextBookingDto;
import ru.practicum.shareit.utils.Create;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class ItemDto {
    private final Long id;
    @NotEmpty(groups = {Create.class}, message = "Название не может быть пустым.")
    private final String name;
    @NotEmpty(groups = {Create.class}, message = "Описание не может быть пустым.")
    private final String description;
    private final Boolean available;
    private final Long request;
    private final LastBookingDto lastBooking;
    private final NextBookingDto nextBooking;
    private final List<CommentDtoOut> comments;
}
