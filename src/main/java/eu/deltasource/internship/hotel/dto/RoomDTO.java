package eu.deltasource.internship.hotel.dto;

import java.util.HashSet;
import java.util.Set;

/**
 * Transfer object for room
 */
public class RoomDTO {
	private Set<AbstractCommodityDTO> commodities;

	public RoomDTO(Set<AbstractCommodityDTO> commodities) {
		this.commodities = new HashSet<>(commodities);
	}

	public Set<AbstractCommodityDTO> getCommodities() {
		return commodities;
	}
}
