package com.howalog.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class HowalogException extends RuntimeException {

    private Map<String, String> validation = new HashMap<>();
    public HowalogException(String message) {
        super(message);
    }

    public HowalogException(Throwable cause) {
        super(cause);
    }

    public void addValidation(String fieldName, String errorMessage) {
        validation.put(fieldName, errorMessage);
    }

    public abstract int getStatusCode();
}
