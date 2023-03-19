package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class ItemDto {
    @NonNull
    @NotBlank(message = "Название не может быть пустым.")
    private final String name;
    @NonNull
    @NotBlank(message = "Название не может быть пустым.")
    private final String description;
    @NonNull
    private final Boolean available;
    private final Long request;
}
