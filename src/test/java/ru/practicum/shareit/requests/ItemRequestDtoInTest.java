package ru.practicum.shareit.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoInTest {
    private JacksonTester<ItemRequestDtoIn> json;
    private ItemRequestDtoIn itemRequestDtoIn;
    private Validator validator;

    public ItemRequestDtoInTest(@Autowired JacksonTester<ItemRequestDtoIn> json) {
        this.json = json;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void beforeEach() {
        itemRequestDtoIn = new ItemRequestDtoIn(
                "some description"
        );
    }

    @Test
    void testJsonItemRequestDto() throws Exception {
        JsonContent<ItemRequestDtoIn> result = json.write(itemRequestDtoIn);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("some description");
    }

    @Test
    void whenItemRequestDtoIsValidThenNoViolations() {
        Set<ConstraintViolation<ItemRequestDtoIn>> violations = validator.validate(itemRequestDtoIn);
        assertThat(violations).isEmpty();
    }

    @Test
    void testDescriptionIsEmpty() {
        itemRequestDtoIn.setDescription(" ");
        Set<ConstraintViolation<ItemRequestDtoIn>> violations = validator.validate(itemRequestDtoIn);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("Описание не может быть пустым.");
    }

    @Test
    void testDescriptionIsBlank() {
        itemRequestDtoIn.setDescription(null);
        Set<ConstraintViolation<ItemRequestDtoIn>> violations = validator.validate(itemRequestDtoIn);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("Описание не может быть пустым.");
    }
}
