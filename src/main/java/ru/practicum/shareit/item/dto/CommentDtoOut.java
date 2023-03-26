package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.utils.Create;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class CommentDtoOut {
    private final Long id;
    @NotEmpty(groups = {Create.class}, message = "Комментарий не может быть пустым.")
    private final String text;
    private final String authorName;
    private final LocalDateTime created;
}
