package eu.deltasource.internship.hotel.exception;

import java.security.InvalidParameterException;

public class ArgumentNotValidException extends InvalidParameterException {
	public ArgumentNotValidException() {
	}

	public ArgumentNotValidException(String message) {
		super(message);
	}
}
