package eu.deltasource.internship.hotel.controller;

import eu.deltasource.internship.hotel.domain.Guest;
import eu.deltasource.internship.hotel.service.GuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/guests")
public class GuestController {

	@Autowired
	private GuestService guestService;

	@GetMapping(value = "/{id}")
	public Guest findById(@PathVariable("id") int id) {
		return this.guestService.findById(id);
	}

	@GetMapping
	public List<Guest> findAll() {
		return this.guestService.findAll();
	}

	@PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Guest updateGuest(@RequestBody Guest guest) {
		return this.guestService.updateGuest(guest);
	}

	@DeleteMapping(value = "/{id}")
	public boolean deleteById(@PathVariable("id") int id) {
		return this.guestService.deleteById(id);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public void saveAll(@RequestBody List<Guest> items) {
		this.guestService.saveAll(items);
	}
}
