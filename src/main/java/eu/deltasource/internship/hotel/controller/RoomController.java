package eu.deltasource.internship.hotel.controller;

import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.service.RoomService;
import eu.deltasource.internship.hotel.dto.RoomDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

	@Autowired
	private RoomService roomService;

	@PostMapping
	public Room saveRoom(@RequestBody RoomDTO room) {
		return roomService.saveRoom(roomService.convertRoomDtoToRoom(room));
	}

	@PostMapping(value = "/list")
	public List<Room> saveRooms(@RequestBody List<RoomDTO> rooms) {
		return roomService.saveRooms(roomService.convertMultipleRoomDtoToRoomList(rooms));
	}

	@GetMapping(value = "/{id}")
	public Room getRoomById(@PathVariable("id") int id) {
		return roomService.getRoomById(id);
	}

	@GetMapping
	public List<Room> findRooms() {
		return roomService.findRooms();
	}

	@PutMapping
	public Room updateRoom(@RequestBody RoomDTO room) {
		return roomService.updateRoom(roomService.convertRoomDtoToRoom(room));
	}

	@DeleteMapping
	public boolean deleteRoom(@RequestBody RoomDTO room) {
		return roomService.deleteRoom(roomService.convertRoomDtoToRoom(room));
	}

	@DeleteMapping(value = "/{id}")
	public boolean deleteRoomById(@PathVariable("id") int id) {
		return roomService.deleteRoomById(id);
	}

	@DeleteMapping(value = "/all")
	public void deleteAll() {
		roomService.deleteAll();
	}
}
