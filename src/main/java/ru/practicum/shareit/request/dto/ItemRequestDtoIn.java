package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDtoIn {
    @NotBlank(message = "Описание не может быть пустым.")
    private String description;
}
