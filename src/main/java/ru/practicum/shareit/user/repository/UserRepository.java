package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.repository.ItemRepositoryCustom;
import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

}
