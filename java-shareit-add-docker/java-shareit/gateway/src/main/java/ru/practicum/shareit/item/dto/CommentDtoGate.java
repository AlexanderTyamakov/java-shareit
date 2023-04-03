package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentDtoGate {
    @NotBlank(message = "Комментарий не может быть пустым.")
    private String text;
}
