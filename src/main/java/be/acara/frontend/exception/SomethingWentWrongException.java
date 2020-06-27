package be.acara.frontend.exception;

import org.springframework.http.HttpStatus;

/**
 * Generic 500 error, in case there isn't a more specific error to be thrown.
 */
public class SomethingWentWrongException extends CustomException {
    public SomethingWentWrongException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Error", "Something went wrong!");
    }
}
