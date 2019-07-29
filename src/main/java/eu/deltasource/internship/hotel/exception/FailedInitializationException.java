package eu.deltasource.internship.hotel.exception;

/**
 * Created by Taner Ilyazov - Delta Source Bulgaria on 2019-07-28.
 */
public class FailedInitializationException extends RuntimeException {

    public FailedInitializationException() {
    }

    public FailedInitializationException(String message) {
        super(message);
    }

    public FailedInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
