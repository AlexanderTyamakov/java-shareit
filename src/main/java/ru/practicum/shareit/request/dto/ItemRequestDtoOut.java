package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ItemRequestDtoOut {
    private Long id;
    @NotBlank
    private String description;
    private UserDto requestor;
    private LocalDateTime created;
    private List<Item> items;
}
