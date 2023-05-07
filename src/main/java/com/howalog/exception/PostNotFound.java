package com.howalog.exception;

/**
 * status : 404
 */
public class PostNotFound extends HowalogException {

    private static final String MESSAGE = "존재하지 않는 게시글 입니다.";

    public PostNotFound() {
        super(MESSAGE);
    }

    public PostNotFound(Throwable cause) {
        super(cause);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
