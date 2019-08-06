package eu.deltasource.internship.hotel.domain.commodity;

import lombok.Getter;

/**
 * Represents different types of beds
 */
@Getter
public enum BedType {
	SINGLE(1), DOUBLE(2), KING_SIZE(2);

	private final int size;

	BedType(int size) {
		this.size = size;
	}
}
