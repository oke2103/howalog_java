package com.howalog.exception;

import lombok.Getter;

/**
 * status : 400
 */
@Getter
public class InvalidException extends HowalogException {

    private static final String MESSAGE = "제목에 욕설을 포함할 수 없습니다.";
    public InvalidException() {
        super(MESSAGE);
    }
    public InvalidException(Throwable cause) {
        super(cause);
    }

    public InvalidException(String fieldName, String errorMessage) {
        super(MESSAGE);
        addValidation(fieldName, errorMessage);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
