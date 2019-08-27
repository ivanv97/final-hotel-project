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
	 * Constructor - takes repository
	 * object and assigns it to field
	 *
	 * @param roomRepository rooms repository
	 */
	@Autowired
	public RoomService(RoomRepository roomRepository) {
		this.roomRepository = roomRepository;
	}

	/**
	 * Returns a list of all the rooms - if any
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
	 * @throws ItemNotFoundException if the room we want to get
	 *                               does not exist
	 */
	public Room getRoomById(int id) {
		if (!roomRepository.existsById(id)) {
			throw new ItemNotFoundException("Room with id " + id + " does not exist!");
		}
		return roomRepository.findById(id);
	}

	/**
	 * Creates a new room
	 * First checks if room instance
	 * is valid
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
	 * Creates multiple rooms by passed varargs
	 * Checks each room for validity beforehand
	 *
	 * @param rooms array of rooms
	 * @return all rooms in the repo
	 */
	public List<Room> saveRooms(Room... rooms) {
		for (Room room : rooms) {
			validateRoom(room);
		}
		roomRepository.saveAll(rooms);
		return findRooms();
	}

	/**
	 * Creates all the rooms in the passed list of rooms
	 *
	 * @param rooms list of room to be saved
	 * @return all rooms in the repo
	 */
	public List<Room> saveRooms(List<Room> rooms) {
		return saveRooms(rooms.toArray(new Room[rooms.size()]));
	}

	/**
	 * Updates existing room
	 * Takes room argument, as we want
	 * it to be updated and validates it
	 *
	 * @param room the way we want the room to look like
	 * @return the updated room
	 * @throws ItemNotFoundException if the room we're
	 *                               trying to update doesn't exist
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
	 * @throws ItemNotFoundException if room we try to delete
	 *                               a non-existing room
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
	 * @throws ItemNotFoundException if room we try to delete
	 *                               a non-existing room
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
	 * Converts List of RoomDto objects
	 * to List of Room objects
	 *
	 * @param roomsDTO list of DTO objects
	 * @return list of model objects
	 * @throws ArgumentNotValidException if the list of dtos is null or is empty
	 */
	public List<Room> convertMultipleRoomDtoToRoomList(List<RoomDTO> roomsDTO) {
		if (roomsDTO == null || roomsDTO.isEmpty()) {
			throw new ArgumentNotValidException("RoomDTO list not valid!");
		}
		List<Room> rooms = new ArrayList<>();
		for (RoomDTO room : roomsDTO) {
			rooms.add(convertRoomDtoToRoom(room));
		}
		return rooms;
	}

	/**
	 * Converts single RoomDTO object
	 * to ordinary Room object
	 *
	 * @param roomDTO DTO object
	 * @return model object
	 * @throws ArgumentNotValidException if the RoomDTO argument is null
	 */
	public Room convertRoomDtoToRoom(RoomDTO roomDTO) {
		if (roomDTO == null) {
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

	/**
	 * Validates a room -
	 * It should not be null and should have at least
	 * one bed and commodities field should not be null either
	 *
	 * @param room the room to be checked
	 * @throws ArgumentNotValidException if any of the requirements is not satisfied
	 */
	private void validateRoom(Room room) {
		if (room == null || room.getCommodities() == null
			|| room.getCommodities().isEmpty() || room.getCommodities().contains(null)
			|| room.getRoomCapacity() <= 0) {
			throw new ArgumentNotValidException("Invalid room!");
		}
	}
}
