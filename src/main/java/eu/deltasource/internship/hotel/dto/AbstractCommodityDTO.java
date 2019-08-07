package eu.deltasource.internship.hotel.dto;

/**
 * Transfer object for commodities
 */
public class AbstractCommodityDTO {

	protected int inventoryId;

	public AbstractCommodityDTO(int inventoryId) {
		this.inventoryId = inventoryId;
	}

	public AbstractCommodityDTO() {
	}

	public int getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(int inventoryId) {
		this.inventoryId = inventoryId;
	}
}
