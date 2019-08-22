package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.domain.commodity.AbstractCommodity;
import eu.deltasource.internship.hotel.domain.commodity.Bed;
import eu.deltasource.internship.hotel.domain.commodity.Shower;
import eu.deltasource.internship.hotel.domain.commodity.Toilet;
import eu.deltasource.internship.hotel.dto.AbstractCommodityDTO;
import eu.deltasource.internship.hotel.dto.BedDTO;
import eu.deltasource.internship.hotel.dto.ToiletDTO;
import eu.deltasource.internship.hotel.exception.ArgumentNotValidException;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.RoomRepository;

import eu.deltasource.internship.hotel.dto.RoomDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service class for the
 * logic of room actions -
 * adding, updating, deleting
 * and getting rooms
 */
@Service
public class RoomService {

	private final RoomRepository roomRepository;

	/**
	 * This is a constructor
	 *
	 * @param roomRepository rooms repository
	 */
	@Autowired
	public RoomService(RoomRepository roomRepository) {
		this.roomRepository = roomRepository;
	}

	/**
	 * Returns a list of all the rooms - if there are any
	 */
	public List<Room> findRooms() {
		return roomRepository.findAll();
	}

	/**
	 * Searches room by id
	 * and returns one if id is valid
	 *
	 * @param id room's id
	 * @return copy of the found room
	 */
	public Room getRoomById(int id) {
		if (!roomRepository.existsById(id)) {
			throw new ItemNotFoundException("Room with id " + id + " does not exist!");
		}
		return roomRepository.findById(id);
	}

	/**
	 * Creates a new room
	 *
	 * @param room the new room
	 * @return the new room
	 */
	public Room saveRoom(Room room) {
		validateRoom(room);
		roomRepository.save(room);
		return getRoomById(roomRepository.count());
	}

	/**
	 * Creates multiple rooms
	 *
	 * @param rooms array of rooms
	 * @return the new rooms
	 */
	public List<Room> saveRooms(Room... rooms) {
		validateRoomList(rooms);
		roomRepository.saveAll(rooms);
		return findRooms();
	}

	/**
	 * Creates all the rooms in the passed list of rooms
	 *
	 * @param rooms
	 * @return list of the new created rooms
	 */
	public List<Room> saveRooms(List<Room> rooms) {
		return saveRooms(rooms.toArray(new Room[rooms.size()]));
	}

	/**
	 * Updates existing room
	 *
	 * @param room the room that will be updated
	 * @return the updated room
	 */
	public Room updateRoom(Room room) {
		validateRoom(room);
		if (!roomRepository.existsById(room.getRoomId())) {
			throw new ItemNotFoundException("Room with " + " does not exist!");
		}
		return roomRepository.updateRoom(room);
	}

	/**
	 * Deletes room by id
	 *
	 * @param id room's id
	 * @return true if the room is successfully deleted/removed
	 */
	public boolean deleteRoomById(int id) {
		if (!roomRepository.existsById(id)) {
			throw new ItemNotFoundException("Room with id " + id + " does not exist!");
		}
		return roomRepository.deleteById(id);
	}

	/**
	 * Deletes the room with
	 * matching id
	 *
	 * @param room the room that will be deleted
	 * @return true if the room was successfully deleted
	 */
	public boolean deleteRoom(Room room) {
		validateRoom(room);
		if (!roomRepository.existsById(room.getRoomId())) {
			throw new ItemNotFoundException("Cannot delete non-existing room!");
		}
		return roomRepository.delete(getRoomById(room.getRoomId()));
	}

	/**
	 * Deletes all rooms in repository
	 */
	public void deleteAll() {
		roomRepository.deleteAll();
	}

	/**
	 * Converts multiple room DTO objects
	 * to model objects
	 *
	 * @param roomsDTO list of DTO objects
	 * @return list of model objects
	 */
	public List<Room> convertDTORoomsToModel(List<RoomDTO> roomsDTO) {
		List<Room> rooms = new ArrayList<>();
		for (RoomDTO room : roomsDTO) {
			rooms.add(convertDTORoomToModel(room));
		}
		return rooms;
	}

	/**
	 * Converts single DTO roomDTO object
	 * to ordinary model object
	 *
	 * @param roomDTO DTO object
	 * @return model object
	 */
	public Room convertDTORoomToModel(RoomDTO roomDTO) {
		if (roomDTO == null || roomDTO.getCommodities() == null
			|| roomDTO.getCommodities().contains(null)) {
			throw new ArgumentNotValidException("Invalid roomDTO transfer object!");
		}
		int roomId = roomDTO.getRoomId();
		Set<AbstractCommodity> roomCommodities = new HashSet<>();
		for (AbstractCommodityDTO commodityDTO : roomDTO.getCommodities()) {
			if (commodityDTO instanceof BedDTO) {
				Bed bed = new Bed(((BedDTO) commodityDTO).getBedType());
				roomCommodities.add(bed);
			} else if (commodityDTO instanceof ToiletDTO) {
				roomCommodities.add(new Toilet());
			} else {
				roomCommodities.add(new Shower());
			}
		}
		return new Room(roomId, roomCommodities);
	}

	private void validateRoomList(Room... rooms) {
		if (rooms == null) {
			throw new ArgumentNotValidException("Invalid rooms !");
		}
		for (Room room : rooms) {
			validateRoom(room);
		}
	}

	private void validateRoom(Room room) {
		if (room == null || room.getCommodities() == null
			|| room.getCommodities().isEmpty() || room.getCommodities().contains(null)
			|| room.getRoomCapacity() <= 0) {
			throw new ArgumentNotValidException("Invalid room!");
		}
	}
}
