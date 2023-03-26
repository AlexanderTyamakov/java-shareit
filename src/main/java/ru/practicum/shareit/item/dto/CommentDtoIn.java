package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentDtoIn {
    @NotEmpty(message = "Комментарий не может быть пустым.")
    private String text;
}
