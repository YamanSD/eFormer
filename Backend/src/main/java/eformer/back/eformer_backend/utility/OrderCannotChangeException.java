package eformer.back.eformer_backend.utility;

public class OrderCannotChangeException extends RuntimeException {
    public OrderCannotChangeException(String msg) {
        super(msg);
    }
}
