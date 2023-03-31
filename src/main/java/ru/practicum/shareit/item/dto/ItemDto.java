package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.LastBookingDto;
import ru.practicum.shareit.booking.dto.NextBookingDto;
import ru.practicum.shareit.utils.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class ItemDto {
    private Long id;
    @NotBlank(groups = {Create.class}, message = "Название не может быть пустым.")
    private String name;
    @NotBlank(groups = {Create.class}, message = "Описание не может быть пустым.")
    private String description;
    private Boolean available;
    private Long requestId;
    private LastBookingDto lastBooking;
    private NextBookingDto nextBooking;
    private List<CommentDtoOut> comments;
}
