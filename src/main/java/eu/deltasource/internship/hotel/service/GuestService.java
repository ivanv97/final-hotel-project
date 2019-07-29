package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.repository.GuestRepository;

/**
 * Created by Taner Ilyazov - Delta Source Bulgaria on 2019-07-28.
 */
public class GuestService {

    private final GuestRepository guestRepository;

    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }
}
