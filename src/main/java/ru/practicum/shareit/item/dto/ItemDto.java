package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

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
    @NotEmpty(groups = {Create.class}, message = "Доступность не может быть пустым.")
    private final Boolean available;
    private final Long request;
}
