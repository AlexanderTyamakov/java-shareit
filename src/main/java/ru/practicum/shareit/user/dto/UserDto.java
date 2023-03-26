package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class UserDto {
    private final Long id;
    @NotEmpty(groups = {Create.class}, message = "Название не может быть пустым.")
    private final String name;
    @NotEmpty(groups = {Create.class}, message = "Адрес электронной почты не может быть пустым.")
    @Email(groups = {Update.class, Create.class}, message = "Строка должна быть правильно сформированным адресом электронной почты.")
    private final String email;
}
