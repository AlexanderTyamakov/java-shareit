package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoGate;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserGateClient usergateClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Поступил запрос в API Gateway - getAllUsers");
        return usergateClient.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        log.info("Поступил запрос в API Gateway - getUser");
        return usergateClient.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> saveUser(@RequestBody @Validated(Create.class) UserDtoGate userDto) {
        log.info("Поступил запрос в API Gateway - saveUser");
        return usergateClient.saveUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@RequestBody @Validated(Update.class) UserDtoGate userDto, @PathVariable Long id) {
        log.info("Поступил запрос в API Gateway - updateUser");
        return usergateClient.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        log.info("Поступил запрос в API Gateway - deleteUser");
        return usergateClient.deleteUser(id);
    }
}
