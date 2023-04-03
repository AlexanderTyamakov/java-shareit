package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class UserDtoGate {
    private Long id;
    @NotBlank(groups = {Create.class}, message = "Название не может быть пустым.")
    private String name;
    @NotBlank(groups = {Create.class}, message = "Адрес электронной почты не может быть пустым.")
    @Email(groups = {Update.class, Create.class}, message = "Строка должна быть правильно сформированным адресом электронной почты.")
    private String email;
}
