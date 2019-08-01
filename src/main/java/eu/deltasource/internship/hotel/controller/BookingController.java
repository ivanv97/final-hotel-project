package eu.deltasource.internship.hotel.controller;


import eu.deltasource.internship.hotel.domain.Booking;
import eu.deltasource.internship.hotel.domain.Date;
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
        return bookingService.findByID(ID);
    }

    @DeleteMapping(value = "/{ID}")
    public boolean deleteByID(@PathVariable("ID") int ID) {
        return bookingService.deleteByID(ID);
    }

    @PutMapping(value = "/{ID}")
    public void updateBooking(@PathVariable("ID") int bookingID, @RequestBody Date dates) {
        bookingService.updateBooking(bookingID, dates.getFrom(), dates.getTo());
    }
}