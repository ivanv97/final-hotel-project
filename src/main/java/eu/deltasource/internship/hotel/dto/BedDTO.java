package eu.deltasource.internship.hotel.dto;

import eu.deltasource.internship.hotel.domain.commodity.BedType;
import lombok.Getter;

/**
 * Transfer object for bed
 */
public class BedDTO extends AbstractCommodityDTO {
	public BedDTO() {
	}

	public BedDTO(BedType bedType) {
		this.bedType = bedType;
	}

	@Getter
	private BedType bedType;
}
