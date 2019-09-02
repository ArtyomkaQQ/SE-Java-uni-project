package ee.ut.math.tvt.salessystem.exception;

public class DateControllEmptyException extends RuntimeException {

    public DateControllEmptyException() {
    }

    public DateControllEmptyException(String message) {
        super(message);
    }

    public DateControllEmptyException(String message, Throwable cause) {
        super(message, cause);
    }
}
