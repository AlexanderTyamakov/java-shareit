package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface UserRepositoryCustom {

    @Query(value = "select email from users where id != ?1", nativeQuery = true)
    List<String> getAllEmailsExceptUserById(long userId);

    @Modifying(clearAutomatically = true)
    @Query(value = "update User u set u.name = ?1, u.email = ?2 where u.id = ?3")
    void updateUserById(String name, String email, long id);

}
