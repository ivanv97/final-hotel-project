package eu.deltasource.internship.hotel.exception;

/**
 * Exception when bookings are overlapped
 */
public class BookingOverlappingException extends RuntimeException {

    public BookingOverlappingException(String message) {
        super(message);
    }
}
