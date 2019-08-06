package eu.deltasource.internship.hotel.domain.commodity;

import lombok.Getter;

/**
 * Represents bed in a hotel room
 */
@Getter
public class Bed extends AbstractCommodity {

    private final BedType bedType;

    public Bed(BedType bedType) {
        super();
        this.bedType = bedType;

    }

    public BedType getBedType() {
        return bedType;
    }

    public int getSize() {
        return bedType.getSize();
    }

    @Override
    public void prepare() {
        System.out.println("The bed sheets are being replaced!");
    }
}
