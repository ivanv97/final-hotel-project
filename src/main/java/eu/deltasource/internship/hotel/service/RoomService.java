package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.repository.RoomRepository;

import java.util.List;

/**
 * Created by Taner Ilyazov - Delta Source Bulgaria on 2019-07-28.
 */
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room getRoomById(int id) {
        return roomRepository.findById(id);
    }

    public List<Room> findRooms() {
        return roomRepository.findAll();
    }

    public Room saveRoom(Room room) {
        roomRepository.save(room);
        return roomRepository.findById(room.getRoomId());
    }

    public void saveRooms(Room... rooms) {
        roomRepository.saveAll(rooms);
    }

    public boolean deleteRoom(Room room) {
        return roomRepository.delete(room);
    }

    public boolean deleteRoomById(int id) {
        return roomRepository.deleteById(id);
    }

    public Room updateRoom(Room room) {
        return roomRepository.updateRoom(room);
    }
}
