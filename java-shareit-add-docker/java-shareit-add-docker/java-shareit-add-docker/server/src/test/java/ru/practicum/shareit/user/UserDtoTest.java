package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {
    private JacksonTester<UserDto> json;
    private UserDto userDto;
    private Validator validator;

    public UserDtoTest(@Autowired JacksonTester<UserDto> json) {
        this.json = json;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void beforeEach() {
        userDto = new UserDto(
                1L,
                "Alex",
                "alex@alex.ru"
        );
    }

    @Test
    void testJsonUserDto() throws Exception {
        JsonContent<UserDto> result = json.write(userDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Alex");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("alex@alex.ru");
    }

    @Test
    void whenUserDtoIsValidThenNoViolations() {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenCreateUserDtoNameIsBlankThenViolationsShouldBeReportedNotBlank() {
        userDto.setName(" ");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Create.class);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("Название не может быть пустым.");
    }

    @Test
    void whenCreateUserDtoNameIsNullThenViolationsShouldBeReportedNotBlank() {
        userDto.setName(null);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Create.class);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("Название не может быть пустым.");
    }

    @Test
    void whenCreateUserDtoEmailIsBlankThenViolationsShouldBeReportedNotBlank() {
        userDto.setEmail(" ");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Create.class);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("Адрес электронной почты не может быть пустым.");
    }

    @Test
    void whenCreateUserDtoEmailNotEmailThenViolationsShouldBeReportedNotEmail() {
        userDto.setEmail("alex.alex");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Create.class);
        assertThat(violations).isNotEmpty();
        System.out.println(violations.toString());
        assertThat(violations.toString()).contains("Строка должна быть правильно сформированным адресом электронной почты.");
    }

    @Test
    void whenCreateUserDtoEmailIsNullThenViolationsShouldBeReportedNotBlank() {
        userDto.setEmail(null);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Create.class);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("Адрес электронной почты не может быть пустым.");
    }

    @Test
    void whenUpdateUserDtoEmailNotEmailThenViolationsShouldBeReportedNotEmail() {
        userDto.setEmail("alex.alex");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Update.class);
        assertThat(violations).isNotEmpty();
        System.out.println(violations.toString());
        assertThat(violations.toString()).contains("Строка должна быть правильно сформированным адресом электронной почты.");
    }
}
