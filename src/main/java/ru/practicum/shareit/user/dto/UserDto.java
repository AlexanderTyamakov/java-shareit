package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class UserDto {
    private Long id;
    @NotBlank(groups = {Create.class}, message = "Название не может быть пустым.")
    private String name;
    @NotBlank(groups = {Create.class}, message = "Адрес электронной почты не может быть пустым.")
    @Email(groups = {Update.class, Create.class}, message = "Строка должна быть правильно сформированным адресом электронной почты.")
    private String email;
}
