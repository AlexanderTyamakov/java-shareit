package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.Item;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemRequestDtoIn {
    @NotBlank(message = "Описание не может быть пустым.")
    private String description;
}
