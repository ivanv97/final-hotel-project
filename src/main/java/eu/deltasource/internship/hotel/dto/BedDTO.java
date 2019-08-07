package eu.deltasource.internship.hotel.dto;

import eu.deltasource.internship.hotel.domain.commodity.BedType;

/**
 * Transfer object for bed
 */

public class BedDTO extends AbstractCommodityDTO {

	private BedType bedType;

	public BedDTO(BedType bedType) {
		super();
		this.bedType = bedType;
	}

	public BedType getBedType() {
		return bedType;
	}

	public void setBedType(BedType bedType) {
		this.bedType = bedType;
	}

	public int getBedSize() {
		return bedType.getSize();
	}
}
