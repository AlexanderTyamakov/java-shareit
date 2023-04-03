package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.utils.Create;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class ItemDtoGate {
    private Long id;
    @NotBlank(groups = {Create.class}, message = "Название не может быть пустым.")
    private String name;
    @NotBlank(groups = {Create.class}, message = "Описание не может быть пустым.")
    private String description;
    private Boolean available;
    private Long requestId;
}
