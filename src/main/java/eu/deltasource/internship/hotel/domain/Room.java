package eu.deltasource.internship.hotel.domain;

import eu.deltasource.internship.hotel.domain.commodity.AbstractCommodity;
import eu.deltasource.internship.hotel.domain.commodity.Bed;
import eu.deltasource.internship.hotel.exception.FailedInitializationException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Taner Ilyazov - Delta Source Bulgaria on 2019-07-28.
 */
public class Room {

    private static final int EMPTY_ROOM = 0;

    private int roomId;
    private int roomCapacity;
    private final Set<AbstractCommodity> commodities;

    public Room(int roomId, Set<AbstractCommodity> commodities) {
        this.roomId = roomId;
        this.commodities = new HashSet<>();
        updateCommodities(commodities);
    }

    public Room(Room room) {
        this(room.roomId, room.getCommodities());
    }

    public int getRoomId() {
        return roomId;
    }

    public int getRoomCapacity() {
        return roomCapacity;
    }

    public Set<AbstractCommodity> getCommodities() {
        return Collections.unmodifiableSet(commodities);
    }

    private void roomCapacitySetter() {
        roomCapacity = 0;
        for (AbstractCommodity commodity : commodities) {
            if (commodity instanceof Bed) {
                roomCapacity += ((Bed) commodity).getSize();
            }
        }
        if (roomCapacity == EMPTY_ROOM) {
            throw new FailedInitializationException("Room can not be empty");
        }
    }

    public void updateCommodities(Set<AbstractCommodity> commodities) {
        if (commodities == null || commodities.isEmpty()) {
            throw new FailedInitializationException("Room has no commodities!");
        }
        this.commodities.clear();
        this.commodities.addAll(commodities);
        roomCapacitySetter();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Room)) {
            return false;
        }
        return roomId == ((Room) obj).roomId;
    }

    @Override
    public int hashCode() {
        return roomId;
    }
}
