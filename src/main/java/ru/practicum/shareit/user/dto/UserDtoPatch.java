package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class UserDtoPatch {
    private final String name;
    @Email(message = "Строка должна быть правильно сформированным адресом электронной почты.")
    private final String email;
}
