package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.repository.BookingRepository;

/**
 * Created by Taner Ilyazov - Delta Source Bulgaria on 2019-07-28.
 */
public class BookingService {

    private final BookingRepository bookingRepository;

    private final RoomService roomService;

    private final GuestService guestService;

    public BookingService(BookingRepository bookingRepository, RoomService roomService, GuestService guestService) {
        this.bookingRepository = bookingRepository;
        this.roomService = roomService;
        this.guestService = guestService;
    }
}
