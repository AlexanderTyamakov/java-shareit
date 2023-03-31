package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utils.Create;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {

    private JacksonTester<ItemDto> json;
    private ItemDto itemDto;
    private Validator validator;

    public ItemDtoTest(@Autowired JacksonTester<ItemDto> json) {
        this.json = json;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void beforeEach() {
        itemDto = new ItemDto(
                1L,
                "name",
                "description",
                false,
                5L,
                null,
                null,
                new ArrayList<>()
        );
    }

    @Test
    void testJsonItemDto() throws Exception {
        JsonContent<ItemDto> result = json.write(itemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isFalse();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(5);
    }

    @Test
    void whenItemDtoIsValidThenNoViolations() {
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, Create.class);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenCreateItemDtoNameIsBlankThenViolationsShouldBeReportedNotBlank() {
        itemDto.setName(" ");
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, Create.class);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("Название не может быть пустым.");
    }

    @Test
    void whenCreateItemDtoNameIsNullThenViolationsShouldBeReportedNotBlank() {
        itemDto.setName(null);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, Create.class);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("Название не может быть пустым.");
    }

    @Test
    void whenCreateItemDtoDescriptionIsBlankThenViolationsShouldBeReportedNotBlank() {
        itemDto.setDescription(" ");
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, Create.class);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("Описание не может быть пустым.");
    }

    @Test
    void whenCreateItemDtoDescriptionIsNullThenViolationsShouldBeReportedNotBlank() {
        itemDto.setDescription(null);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, Create.class);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("Описание не может быть пустым.");
    }
}
