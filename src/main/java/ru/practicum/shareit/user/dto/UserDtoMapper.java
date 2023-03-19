package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public class UserDtoMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserDto user, Long id) {
        return new User(
                id,
                user.getName(),
                user.getEmail()
        );
    }

    public static User patchToUser(UserDtoPatch userDtoPatch, User user, Long id) {
        return new User(
                id,
                (userDtoPatch.getName() != null && !userDtoPatch.getName().isEmpty()) ? userDtoPatch.getName() : user.getName(),
                (userDtoPatch.getEmail() != null && !userDtoPatch.getEmail().isEmpty()) ? userDtoPatch.getEmail() : user.getEmail()
        );
    }
}
