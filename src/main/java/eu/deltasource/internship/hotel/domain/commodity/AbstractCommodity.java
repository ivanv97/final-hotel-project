package eu.deltasource.internship.hotel.domain.commodity;

import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * Created by Taner Ilyazov - Delta Source Bulgaria on 2019-07-28.
 */
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
