package eu.deltasource.internship.hotel.controllers;

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

	@GetMapping(value = "/{id}")
	public Room getRoomById(@PathVariable("id") int id) {
		return this.roomService.getRoomById(id);
	}

	@GetMapping
	public List<Room> findRooms() {
		return this.roomService.findRooms();
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public void saveRoom(@RequestBody Room room) {
		roomService.saveRoom(room);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public void saveRooms(@RequestBody Room... rooms) {
		this.roomService.saveRooms(rooms);
	}

	@DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public boolean deleteRoom(@RequestBody Room room) {
		return this.roomService.deleteRoom(room);
	}

	@DeleteMapping(value = "/{id}")
	public boolean deleteRoomById(@PathVariable("id") int id) {
		return this.roomService.deleteRoomById(id);
	}

	@PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Room updateRoom(@RequestBody Room room) {
		return this.roomService.updateRoom(room);
	}
}