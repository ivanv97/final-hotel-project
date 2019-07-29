package eu.deltasource.internship.hotel.domain.commodity;

/**
 * Created by Taner Ilyazov - Delta Source Bulgaria on 2019-07-28.
 */
public enum BedType {
    SINGLE(1), DOUBLE(2), KING_SIZE(2);

    private final int size;

    BedType(int size){
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}