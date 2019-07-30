package eu.deltasource.internship.hotel.controller;

import eu.deltasource.internship.hotel.domain.Booking;
import eu.deltasource.internship.hotel.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void save(@RequestBody Booking newBooking) {
        bookingService.save(newBooking);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Booking> findAll(@RequestBody List<Booking> bookings) {
        return bookingService.findAll();
    }

    @GetMapping("/id")
    public Booking findById(@RequestBody int ID) {
        return bookingService.findByID(ID);
    }

    @DeleteMapping("/id")
    public boolean deleteByID(@RequestBody int ID) {
        return bookingService.deleteByID(ID);
    }

    @PutMapping("/id")
    public void updateBooking(@RequestBody int bookingID, LocalDate from, LocalDate to) {
        bookingService.deleteByID(bookingID);
    }
}