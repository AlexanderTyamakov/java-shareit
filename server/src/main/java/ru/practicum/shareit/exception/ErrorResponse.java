package ru.practicum.shareit.exception;

public class ErrorResponse {
    private final String description;
    private final String error;

    public ErrorResponse(String description, String error) {
        this.description = description;
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }
}
