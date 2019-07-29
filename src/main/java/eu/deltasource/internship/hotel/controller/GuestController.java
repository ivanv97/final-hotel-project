package eu.deltasource.internship.hotel.controller;

import eu.deltasource.internship.hotel.domain.Guest;
import eu.deltasource.internship.hotel.service.GuestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
@RequestMapping("/guests")
public class GuestController {

    private GuestService guestService;


    @PostMapping("/guests")
    public void createGuest(Guest guest) {
        this.guestService.save(guest);
    }

    @GetMapping("/guests/id")
    public List<Guest> findAll() {
        return this.guestService.findAll();
    }
}
