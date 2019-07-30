package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Guest;
import eu.deltasource.internship.hotel.exception.FailedInitializationException;
import eu.deltasource.internship.hotel.repository.GuestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Represents guest serivce
 */
@Service
public class GuestService {

    private final GuestRepository guestRepository;

    /**
     * This is a constructor
     *
     * @param guestRepository
     */
    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    /**
     * Finds all guests
     *
     * @return list of guests
     */
    public List<Guest> findAll() {
        return this.guestRepository.findAll();
    }

    /**
     * Creates new guest
     *
     * @param newGuest the new guest
     */
    public void save(Guest newGuest) {
        if (newGuest == null) {
            throw new FailedInitializationException("The guest can not be created !");
        }
        this.guestRepository.save(newGuest);
    }
}
