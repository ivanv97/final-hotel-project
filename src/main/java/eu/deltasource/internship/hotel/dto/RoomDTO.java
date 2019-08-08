package eu.deltasource.internship.hotel.dto;

import java.util.HashSet;
import java.util.Set;

/**
 * Transfer object for room
 */
public class RoomDTO {

	private int roomId;
	private Set<AbstractCommodityDTO> commodities;

	public RoomDTO(int roomId, Set<AbstractCommodityDTO> commodities) {
		this.roomId = roomId;
		this.commodities = commodities;
	}

	public Set<AbstractCommodityDTO> getCommodities() {
		return commodities;
	}

	public int getRoomId() {
		return roomId;
	}
}
