package be.acara.frontend.exception;

public class IdNotFoundException extends NotFoundException{
    public IdNotFoundException(String message) {
        super("Cannot process entry", message);
    }
}
