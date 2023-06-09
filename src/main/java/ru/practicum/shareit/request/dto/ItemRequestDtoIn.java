package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDtoIn {
    private Long id;
    @NotBlank(message = "Описание не может быть пустым.")
    private String description;
    private LocalDateTime created;
}
