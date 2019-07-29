package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Hotel;
import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.domain.commodity.*;
import eu.deltasource.internship.hotel.repository.BookingRepository;
import eu.deltasource.internship.hotel.repository.GuestRepository;
import eu.deltasource.internship.hotel.repository.RoomRepository;
import org.junit.Before;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static eu.deltasource.internship.hotel.domain.commodity.BedType.SINGLE;

/**
 * This test class has a ready initialization for the rooms of the hotel,
 * which you can use for testing! Add more when needed.
 *
 */
public class TestConfiguration {

    @Before
    public void setUp() {

        // Initialize Repositories
        BookingRepository bookingRepository = new BookingRepository();
        GuestRepository guestRepository = new GuestRepository();
        RoomRepository roomRepository = new RoomRepository();

        // Initialize Services
        RoomService roomService = new RoomService(roomRepository);
        GuestService guestService = new GuestService(guestRepository);
        BookingService bookingService = new BookingService(bookingRepository, roomService, guestService);

        // Initialize Hotel (API), which does nothing

        Hotel hotel = new Hotel(bookingService, guestService, roomService);

        // Filling up hotel with ready rooms to use

        // Comodities for a double room
        AbstractCommodity doubleBed = new Bed(BedType.DOUBLE);
        AbstractCommodity toilet = new Toilet();
        AbstractCommodity shower = new Shower();

        Set<AbstractCommodity> doubleSet = new HashSet<>(Arrays.asList(doubleBed, toilet, shower));

        // commodities for a single room
        Set<AbstractCommodity> singleSet = new HashSet<>(Arrays.asList(new Bed(SINGLE), new Toilet(), new Shower()));

        // commodities for a double room with king size bed
        Set<AbstractCommodity> kingSizeSet = new HashSet<>(Arrays.asList(new Bed(BedType.KING_SIZE), new Toilet(), new Shower()));

        // commodities for a 3 person room with a king size and a single
        Set<AbstractCommodity> threePeopleKingSizeSet = new HashSet<>(Arrays.asList(new Bed(BedType.KING_SIZE), new Bed(SINGLE), new Toilet(), new Shower()));

        // commodities for a 4 person room with 2 doubles
        Set<AbstractCommodity> fourPersonSet = new HashSet<>(Arrays.asList(new Bed(BedType.DOUBLE), new Bed(BedType.DOUBLE), new Toilet(), new Shower()));

        // commodities for a 4 person room with 2 doubles
        Set<AbstractCommodity> fivePersonSet = new HashSet<>(Arrays.asList(new Bed(BedType.KING_SIZE), new Bed(BedType.DOUBLE), new Bed(SINGLE), new Toilet(), new Toilet(), new Shower()));

        // create some rooms
        Room doubleRoom = new Room(1, doubleSet);
        Room singleRoom = new Room(1, singleSet);
        Room kingSizeRoom = new Room(1, kingSizeSet);
        Room threePeopleKingSizeRoom = new Room(1, threePeopleKingSizeSet);
        Room fourPersonRoom = new Room(1, fourPersonSet);
        Room fivePersonRoom = new Room(1, fivePersonSet);

        // adds the rooms to the repository, which then can be accesses from the RoomService
        roomService.saveRooms(doubleRoom, singleRoom, kingSizeRoom, threePeopleKingSizeRoom, fourPersonRoom, fivePersonRoom);

    }
}
