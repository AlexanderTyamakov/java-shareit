package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emailUniqSet = new HashSet<>();
    private long userRepositoryId;

    @Override
    public List<User> findAll() {
        return users.values().stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> getById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> save(UserDto userDto) {
        User user;
        if (emailUniqSet.contains(userDto.getEmail())) {
            user = null;
        } else {
            long id = getId();
            user = UserDtoMapper.toUser(userDto, id);
            users.put(id, user);
            emailUniqSet.add(user.getEmail());
        }
        return Optional.ofNullable(user);
    }

    public Optional<User> update(long id, UserDto userDto) {
        User user;
        if ((emailUniqSet.contains(userDto.getEmail())) && (!users.get(id).getEmail().equals(userDto.getEmail()))) {
            user = null;
        } else {
            emailUniqSet.remove(users.get(id).getEmail());
            user = UserDtoMapper.patchToUser(userDto, users.get(id), id);
            users.put(id, user);
            emailUniqSet.add(user.getEmail());
        }
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> delete(long id) {
        emailUniqSet.remove(users.get(id).getEmail());
        return Optional.ofNullable(users.remove(id));
    }

    private long getId() {
        return ++userRepositoryId;
    }
}
