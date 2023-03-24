package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public class UserDtoMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
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

    public static User toNewUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public static User patchToUser(UserDto userDto, User user, Long id) {
        return new User(
                id,
                (userDto.getName() != null && !userDto.getName().isEmpty()) ? userDto.getName() : user.getName(),
                (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) ? userDto.getEmail() : user.getEmail()
        );
    }
}
