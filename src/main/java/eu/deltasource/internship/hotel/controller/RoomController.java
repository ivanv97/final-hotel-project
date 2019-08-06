package eu.deltasource.internship.hotel.controller;

import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

	@Autowired
	private RoomService roomService;

	@PostMapping
	public void saveRoom(@RequestBody Room room) {
		roomService.saveRoom(room);
	}

	@PostMapping(value = "/multiple")
	public void saveRooms(@RequestBody List<Room> rooms) {
		roomService.saveRooms(rooms);
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
	public Room updateRoom(@RequestBody Room room) {
		return roomService.updateRoom(room);
	}

	@DeleteMapping(value = "/room")
	public boolean deleteRoom(@RequestBody Room room) {
		return roomService.deleteRoom(room);
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
