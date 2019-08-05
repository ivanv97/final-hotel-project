package eu.deltasource.internship.hotel.controller;


import eu.deltasource.internship.hotel.domain.Booking;
import eu.deltasource.internship.hotel.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping(value = "/create")
    public void save(@RequestBody Booking newBooking) {
        bookingService.save(newBooking);
    }

    @GetMapping(value = "/findAllBookings")
    public List<Booking> findAll() {
        return bookingService.findAll();
    }

    @GetMapping(value = "/{ID}")
    public Booking findById(@PathVariable("ID") int ID) {
        return bookingService.findById(ID);
    }

    @DeleteMapping(value = "/{ID}")
    public boolean deleteByID(@PathVariable("ID") int ID) {
        return bookingService.deleteById(ID);
    }

    @PutMapping(value = "/{ID}")
    public void updateBooking(@PathVariable("ID") int bookingID, @RequestBody Date dates) {
        bookingService.updateBookingByDates(bookingID, dates.getFrom(), dates.getTo());
    }
}

//import eu.deltasource.internship.hotel.domain.Booking;
//import eu.deltasource.internship.hotel.service.BookingService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;

//@RestController
//@RequestMapping("/bookings")
//public class BookingController {
//	@Autowired
//	private BookingService service;
//
//	@GetMapping(value = "/{id}")
//	public Booking findById(@PathVariable("id") int id) {
//		return service.findById(id);
//	}
//
//	@GetMapping
//	public List<Booking> findAll() {
//		return service.findAll();
//	}
//
//	@PutMapping(value = "/dates", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
//	public Booking updateBookingByDates(@RequestBody Booking booking) {
//		return service.updateBookingByDates(booking);
//	}
//
//	@PutMapping(value = "/room", consumes = MediaType.APPLICATION_JSON_VALUE)
//	public void updateBookingRoom(@RequestBody Booking booking) {
//		service.updateBookingRoom(booking);
//	}
//
//	@DeleteMapping(value = "/{id}")
//	public boolean deleteById(@PathVariable("id") int id) {
//		return service.deleteById(id);
//	}
//
//	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
//	public void saveAll(@RequestBody List<Booking> items) {
//		service.saveAll(items);
//	}
//}
