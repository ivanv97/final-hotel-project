package eu.deltasource.internship.hotel.controller;


import eu.deltasource.internship.hotel.domain.Booking;
import eu.deltasource.internship.hotel.service.BookingService;
import eu.deltasource.internship.hotel.utility.Date;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

	@Autowired
	private BookingService bookingService;

	@PostMapping
	public void save(@RequestBody Booking newBooking) {
		bookingService.save(newBooking);
	}

	@PostMapping(value = "/multiple")
	public void saveAll(@RequestBody List<Booking> bookings) {
		bookingService.saveAll(bookings);
	}

	@GetMapping
	public List<Booking> findAll() {
		return bookingService.findAll();
	}

	@GetMapping(value = "/{id}")
	public Booking findById(@PathVariable("id") int id) {
		return bookingService.findById(id);
	}

	@PutMapping(value = "/{id}/dates")
	public void updateBookingByDates(@PathVariable("id") int bookingId, @RequestBody Date dates) {
		bookingService.updateBookingByDates(bookingId, dates.getFrom(), dates.getTo());
	}

	@PutMapping(value = "/{id}/room")
	public void updateBooking(@PathVariable("id") int bookingId, @RequestBody Booking updatedBooking) {
		bookingService.updateBooking(bookingId, updatedBooking);
	}

	@DeleteMapping(value = "/{id}")
	public boolean deleteById(@PathVariable("id") int id) {
		return bookingService.deleteById(id);
	}

	@DeleteMapping
	public boolean delete(@RequestBody Booking booking) {
		return bookingService.delete(booking);
	}

	@DeleteMapping(value = "/all")
	public void deleteAll() {
		bookingService.deleteAll();
	}
}
