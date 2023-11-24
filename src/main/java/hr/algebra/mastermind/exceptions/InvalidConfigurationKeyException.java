package hr.algebra.mastermind.exceptions;

public class InvalidConfigurationKeyException extends RuntimeException {

    public InvalidConfigurationKeyException() {
    }

    public InvalidConfigurationKeyException(String message) {
        super(message);
    }

    public InvalidConfigurationKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidConfigurationKeyException(Throwable cause) {
        super(cause);
    }

    public InvalidConfigurationKeyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
