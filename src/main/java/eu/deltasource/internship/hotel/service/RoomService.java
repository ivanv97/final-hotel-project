package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.exception.FailedInitializationException;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Represents room service
 */
@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /**
     * Searches room by room's ID
     *
     * @param id room's ID
     * @return the room with that ID
     */
    public Room getRoomById(int id) {
        return roomRepository.findById(id);
    }

    /**
     * Returns all rooms
     *
     * @return all rooms
     */
    public List<Room> findRooms() {
        return roomRepository.findAll();
    }

    /**
     * Creates a new room
     *
     * @param room the new room
     * @return the new room if
     */
    public Room saveRoom(Room room) {
        if (room == null) {
            throw new FailedInitializationException("Invalid room !");
        }
        roomRepository.save(room);
        return roomRepository.findById(room.getRoomId());
    }

    /**
     * Creates array of rooms
     *
     * @param rooms arrays of rooms
     */
    public void saveRooms(Room... rooms) {
        if (rooms == null) {
            throw new FailedInitializationException("Invalid room !");
        }
        roomRepository.saveAll(rooms);
    }

    /**
     * Deletes room
     *
     * @param room the room that will be deleted/removed
     * @return true if the room was successfully deleted/removed
     */
    public boolean deleteRoom(Room room) {
        if (room == null) {
            throw new FailedInitializationException("Invalid room !");
        }
        return roomRepository.delete(room);
    }

    /**
     * Deletes room by id
     *
     * @param id room's id
     * @return true if the room was successfully deleted/removed
     */
    public boolean deleteRoomById(int id) {
        return roomRepository.deleteById(id);
    }

    /**
     * Updates existing room
     *
     * @param room the room that will be updated
     * @return the updated room
     */
    public Room updateRoom(Room room) {
        if (room == null) {
            throw new FailedInitializationException("Invalid room !");
        }
        return roomRepository.updateRoom(room);
    }
}