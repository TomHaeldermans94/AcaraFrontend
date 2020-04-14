package be.acara.frontend.exception;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String message) {
        super("User not found", message);
    }
}
