package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDtoIn;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoInTest {

    private JacksonTester<CommentDtoIn> json;
    private CommentDtoIn commentDtoIn;
    private Validator validator;

    public CommentDtoInTest(@Autowired JacksonTester<CommentDtoIn> json) {
        this.json = json;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void beforeEach() {
        commentDtoIn = new CommentDtoIn(
                "some description"
        );
    }

    @Test
    void testJsonCommentDtoIn() throws Exception {
        JsonContent<CommentDtoIn> result = json.write(commentDtoIn);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("some description");
    }

    @Test
    void whenCommentDtoInIsValidThenNoViolations() {
        Set<ConstraintViolation<CommentDtoIn>> violations = validator.validate(commentDtoIn);
        assertThat(violations).isEmpty();
    }

    @Test
    void testDescriptionIsEmpty() {
        commentDtoIn.setText(" ");
        Set<ConstraintViolation<CommentDtoIn>> violations = validator.validate(commentDtoIn);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("Комментарий не может быть пустым.");
    }

    @Test
    void testDescriptionIsBlank() {
        commentDtoIn.setText(null);
        Set<ConstraintViolation<CommentDtoIn>> violations = validator.validate(commentDtoIn);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("Комментарий не может быть пустым.");
    }
}
