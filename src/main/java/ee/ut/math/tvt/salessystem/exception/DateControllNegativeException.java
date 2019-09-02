package ee.ut.math.tvt.salessystem.exception;

public class DateControllNegativeException extends RuntimeException {

    public DateControllNegativeException() {
    }

    public DateControllNegativeException(String message) {
        super(message);
    }

    public DateControllNegativeException(String message, Throwable cause) {
        super(message, cause);
    }
}
