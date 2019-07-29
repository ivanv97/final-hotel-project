package eu.deltasource.internship.hotel.domain;

import eu.deltasource.internship.hotel.service.BookingService;
import eu.deltasource.internship.hotel.service.GuestService;
import eu.deltasource.internship.hotel.service.RoomService;

/**
 * Right now you can imagine the Hotel as an API Layer,
 * where you can get REST requests and map them to the
 * corresponding services.
 *
 * P.S. Don't mind this layer for now, it serves only as an example.
 * REST Controllers should always be separated.
 *
 * Created by Taner Ilyazov - Delta Source Bulgaria on 2019-07-28.
 */
public class Hotel {

    private final BookingService bookingService;

    private final GuestService guestService;

    private final RoomService roomService;

    /**
     * #thisIsAConstructor
     */
    public Hotel(BookingService bookingService, GuestService guestService, RoomService roomService) {
        this.bookingService = bookingService;
        this.guestService = guestService;
        this.roomService = roomService;
    }
}
