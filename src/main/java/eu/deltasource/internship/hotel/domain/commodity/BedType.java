package eu.deltasource.internship.hotel.domain.commodity;

/**
 * Represents different types of beds
 */
public enum BedType {
    SINGLE(1), DOUBLE(2), KING_SIZE(2);

    private final int size;

    BedType(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}