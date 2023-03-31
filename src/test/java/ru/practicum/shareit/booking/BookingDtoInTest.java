package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoIn;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoInTest {
    private JacksonTester<BookingDtoIn> json;
    private BookingDtoIn bookingDtoIn;
    private Validator validator;

    public BookingDtoInTest(@Autowired JacksonTester<BookingDtoIn> json) {
        this.json = json;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void beforeEach() {
        bookingDtoIn = new BookingDtoIn(
                LocalDateTime.of(2055,10,10,12,30),
                LocalDateTime.of(2060,10,10,12,30),
                2L);
    }

    @Test
    void testJsonBookingDtoIn() throws Exception {
        JsonContent<BookingDtoIn> result = json.write(bookingDtoIn);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2055-10-10T12:30:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2060-10-10T12:30:00");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(2);
    }

    @Test
    void whenCommentDtoInIsValidThenNoViolations() {
        Set<ConstraintViolation<BookingDtoIn>> violations = validator.validate(bookingDtoIn);
        assertThat(violations).isEmpty();
    }



    @Test
    void whenBookingInputDtoStartNotNullThenViolationsShouldBeReportedNotNull() {
        bookingDtoIn.setStart(null);
        Set<ConstraintViolation<BookingDtoIn>> violations = validator.validate(bookingDtoIn);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='must not be null'");
    }

    @Test
    void whenBookingInputDtoEndNotNullThenViolationsShouldBeReportedNotNull() {
        bookingDtoIn.setEnd(null);
        Set<ConstraintViolation<BookingDtoIn>> violations = validator.validate(bookingDtoIn);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='must not be null'");
    }

    @Test
    void whenBookingInputDtoStartBeforeNowThenViolationsShouldBeReportedNotNull() {
        bookingDtoIn.setStart(LocalDateTime.now().minusSeconds(1));
        Set<ConstraintViolation<BookingDtoIn>> violations = validator.validate(bookingDtoIn);
        System.out.println(violations);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("Дата начала не может быть меньше текущей даты");
    }

    @Test
    void whenBookingInputDtoEndBeforeNowThenViolationsShouldBeReportedNotNull() {
        bookingDtoIn.setEnd(LocalDateTime.now().minusSeconds(1));
        Set<ConstraintViolation<BookingDtoIn>> violations = validator.validate(bookingDtoIn);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("Дата начала не может быть меньше текущей даты");
    }

    @Test
    void whenBookingInputDtoItemIdNotNullThenViolationsShouldBeReportedNotNull() {
        bookingDtoIn.setItemId(null);
        Set<ConstraintViolation<BookingDtoIn>> violations = validator.validate(bookingDtoIn);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='must not be null'");
    }
}
