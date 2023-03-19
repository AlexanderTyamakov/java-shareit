package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class UserDto {
    @NotEmpty(message = "Название не может быть пустым.")
    private final String name;
    @NotEmpty(message = "Адрес электронной почты не может быть пустым.")
    @Email(message = "Строка должна быть правильно сформированным адресом электронной почты.")
    private final String email;
}
