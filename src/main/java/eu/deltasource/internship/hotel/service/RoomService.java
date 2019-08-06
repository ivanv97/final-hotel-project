package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.exception.FailedInitializationException;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Represents services for a room
 */
@Service
public class RoomService {

	private final RoomRepository roomRepository;

	/**
	 * This is a constructor
	 *
	 * @param roomRepository rooms repository
	 */
	public RoomService(RoomRepository roomRepository) {
		this.roomRepository = roomRepository;
	}

	/**
	 * Searches room by id
	 *
	 * @param id room's id
	 * @return copy of the found room
	 */
	public Room getRoomById(int id) {
		if (roomRepository.existsById(id)) {
			return roomRepository.findById(id);
		}
		throw new ItemNotFoundException("Room with id " + id + " does not exist!");
	}

	/**
	 * Deletes room by id
	 *
	 * @param id room's id
	 * @return true if the room is successfully deleted/removed
	 */
	public boolean deleteRoomById(int id) {
		if (roomRepository.existsById(id)) {
			return roomRepository.deleteById(id);
		}
		throw new ItemNotFoundException("Room with id " + id + " does not exist!");
	}

	/**
	 * Deletes room
	 *
	 * @param room the room that will be deleted/removed
	 * @return true if the room was successfully deleted/removed
	 */
	public boolean deleteRoom(Room room) {
		validRoom(room);
		return roomRepository.delete(getRoomById(room.getRoomId()));
	}

	/**
	 * Deletes all rooms
	 */
	public void deleteAll() {
		if (roomRepository.count() == 0) {
			throw new ItemNotFoundException("Empty list of rooms can not be deleted!");
		}
		roomRepository.deleteAll();
	}

	/**
	 * Creates a new room
	 *
	 * @param room the new room
	 * @return the new room
	 */
	public void saveRoom(Room room) {
		validRoom(room);
		roomRepository.save(room);
	}

	/**
	 * Creates array of rooms
	 *
	 * @param rooms array of rooms
	 */
	public void saveRooms(Room... rooms) {
		validRooms(rooms);
		roomRepository.saveAll(rooms);
	}

	/**
	 * @return all rooms
	 */
	public List<Room> findRooms() {
		return roomRepository.findAll();
	}

	/**
	 * Updates existing room
	 *
	 * @param room the room that will be updated
	 * @return the updated room
	 */
	public Room updateRoom(Room room) {
		validRoom(room);
		getRoomById(room.getRoomId());
		return roomRepository.updateRoom(room);
	}

	private void validRooms(Room... rooms) {
		if (rooms == null) {
			throw new FailedInitializationException("Invalid rooms !");
		}
		for (Room room : rooms) {
			validRoom(room);
		}
	}

	private void validRoom(Room room) {
		if (room == null || room.getCommodities() == null
			|| room.getCommodities().isEmpty() || room.getCommodities().contains(null)) {
			throw new FailedInitializationException("Invalid room !");
		}
	}
}
