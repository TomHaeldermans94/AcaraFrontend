package be.acara.frontend.exception;

public class CartNotFoundException extends NotFoundException{
    public CartNotFoundException(String message) {
        super("Cart not found", message);
    }
}
