package eu.deltasource.internship.hotel.dto;


import eu.deltasource.internship.hotel.exception.ArgumentNotValidException;
import eu.deltasource.internship.hotel.exception.FailedInitializationException;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * Transfer object for room
 */
@Getter
public class RoomDTO {

	private int roomId;
	private Set<AbstractCommodityDTO> commodities;

	/**
	 * This is a constructor
	 *
	 * @param roomId      room id
	 * @param commodities set of commodities
	 */
	public RoomDTO(int roomId, Set<AbstractCommodityDTO> commodities) {
		this.roomId = roomId;
		this.commodities = new HashSet<>(commodities);
	}

	private void setCommodities(Set<AbstractCommodityDTO> commodities) {
		if (commodities == null || commodities.isEmpty()) {
			throw new FailedInitializationException("Room has no commodities!");
		}
		this.commodities.clear();
		this.commodities.addAll(commodities);
		roomCapacityCheck();
	}

	private void roomCapacityCheck() {
		int roomCapacity = 0;
		for (AbstractCommodityDTO commodity : commodities) {
			if (commodity instanceof BedDTO) {
				roomCapacity += ((BedDTO) commodity).getBedType().getSize();
			}
		}
		if (roomCapacity == 0) {
			throw new FailedInitializationException("Room can not be empty");
		}
	}
}
