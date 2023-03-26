package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.utils.Create;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentDtoIn {
    @NotEmpty(message = "Комментарий не может быть пустым.")
    private String text;
}
