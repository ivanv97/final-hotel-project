package eu.deltasource.internship.hotel.domain.commodity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import org.springframework.stereotype.Component;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
	@JsonSubTypes.Type(value = Bed.class, name = "Bed"),
	@JsonSubTypes.Type(value = Toilet.class, name = "Toilet"),
	@JsonSubTypes.Type(value = Shower.class, name = "Shower")
})
@Component
public abstract class AbstractCommodity {

	@Getter
	protected final int inventoryId;

	private static int INVENTORY_COUNT;

	public AbstractCommodity() {
		this.inventoryId = ++INVENTORY_COUNT;
	}

	public abstract void prepare();

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof AbstractCommodity)) {
			return false;
		}
		return inventoryId == ((AbstractCommodity) obj).inventoryId;
	}

	@Override
	public int hashCode() {
		return inventoryId;
	}
}
