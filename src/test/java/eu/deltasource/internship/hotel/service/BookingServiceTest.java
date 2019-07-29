package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Booking;
import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.domain.commodity.*;
import eu.deltasource.internship.hotel.exception.FailedInitializationException;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.BookingRepository;
import eu.deltasource.internship.hotel.repository.GuestRepository;
import eu.deltasource.internship.hotel.repository.RoomRepository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static eu.deltasource.internship.hotel.domain.commodity.BedType.*;

public class BookingServiceTest {

    BookingRepository bookingRepository = new BookingRepository();
    GuestRepository guestRepository = new GuestRepository();
    RoomRepository roomRepository = new RoomRepository();
    RoomService roomService;
    GuestService guestService;
    BookingService bookingService;

    @Before
    public void setUp() {

        // Initialize Services
        roomService = new RoomService(roomRepository);
        guestService = new GuestService(guestRepository);
        bookingService = new BookingService(bookingRepository, roomService, guestService);

        // Filling up hotel with ready rooms to use

        // Commodities for a double room
        AbstractCommodity doubleBed = new Bed(BedType.DOUBLE);
        AbstractCommodity toilet = new Toilet();
        AbstractCommodity shower = new Shower();

        Set<AbstractCommodity> doubleSet = new HashSet<>(Arrays.asList(doubleBed, toilet, shower));

        // commodities for a single room
        Set<AbstractCommodity> singleSet = new HashSet<>(Arrays.asList
                (new Bed(SINGLE), new Toilet(), new Shower()));

        // commodities for a double room with king size bed
        Set<AbstractCommodity> kingSizeSet = new HashSet<>(Arrays.asList
                (new Bed(BedType.KING_SIZE), new Toilet(), new Shower()));

        // commodities for a 3 person room with a king size and a single
        Set<AbstractCommodity> threePeopleKingSizeSet = new HashSet<>(Arrays.asList(
                new Bed(BedType.KING_SIZE), new Bed(SINGLE), new Toilet(), new Shower()));

        // commodities for a 4 person room with 2 doubles
        Set<AbstractCommodity> fourPersonSet = new HashSet<>(Arrays.asList(new Bed
                (BedType.DOUBLE), new Bed(BedType.DOUBLE), new Toilet(), new Shower()));

        // commodities for a 4 person room with 2 doubles
        Set<AbstractCommodity> fivePersonSet = new HashSet<>(Arrays.asList(new Bed
                (BedType.KING_SIZE), new Bed(BedType.DOUBLE), new Bed(SINGLE), new Toilet(), new Toilet(), new Shower()));

        // create some rooms
        Room doubleRoom = new Room(1, doubleSet);
        Room singleRoom = new Room(2, singleSet);
        Room kingSizeRoom = new Room(3, kingSizeSet);
        Room threePeopleKingSizeRoom = new Room(4, threePeopleKingSizeSet);
        Room fourPersonRoom = new Room(5, fourPersonSet);
        Room fivePersonRoom = new Room(6, fivePersonSet);

        // adds the rooms to the repository, which then can be accesses from the RoomService
        roomService.saveRooms(doubleRoom, singleRoom, kingSizeRoom, threePeopleKingSizeRoom, fourPersonRoom, fivePersonRoom);


        LocalDate firstFrom = LocalDate.of(2019, 8, 12);
        LocalDate firstTo = LocalDate.of(2019, 8, 18);
        Booking firstBooking = new Booking(1, 101, 1, 3, firstFrom, firstTo);

        LocalDate secondFrom = LocalDate.of(2019, 9, 18);
        LocalDate secondTo = LocalDate.of(2019, 9, 21);
        Booking secondBooking = new Booking(2, 345, 4, 3, secondFrom, secondTo);

        LocalDate thirdFrom = LocalDate.of(2019, 7, 30);
        LocalDate thirdTo = LocalDate.of(2019, 7, 31);
        Booking thirdBooking = new Booking(3, 465, 4, 3, thirdFrom, thirdTo);

        bookingService.save(firstBooking);
        bookingService.save(secondBooking);
        bookingService.save(thirdBooking);
    }

    @Test
    public void findByID() {
        //given
        int bookingID = 3;

        //when
        Booking booking = bookingService.findByID(bookingID);

        // then
        assertTrue(bookingID == booking.getBookingId());
    }

    @Test
    public void notFoundID() {
        // given
        int bookID = -5;

        //when and then
        assertThrows(ItemNotFoundException.class, () -> bookingService.findByID(bookID));
    }

    @Test
    public void deleteByExistingID() {
        //given
        int bookingID = 3;

        //when
        boolean result = bookingService.deleteByID(bookingID);

        // then
        assertEquals(true, result);
    }

    @Test
    public void deleteByIDThatDoesNotExists() {
        //given
        int bookingID = 56;

        //when
        assertThrows(ItemNotFoundException.class, () -> bookingService.deleteByID(bookingID));
    }

    /**
     * Under construction
     */
    @Test
    public void updateDates() {
        LocalDate fromDate = LocalDate.of(2019, 12, 7);
        LocalDate toDate = LocalDate.of(2019, 12, 12);
        Booking updateBooking = new Booking(1, 101, 1, 2, fromDate, toDate);

    }

    @Test
    public void createBookingSuccessfully() {
        //given
        LocalDate fourthFrom = LocalDate.of(2019, 10, 3);
        LocalDate fourthTo = LocalDate.of(2019, 10, 8);
        Booking newBooking = new Booking(4, 583, 6, 5, fourthFrom, fourthTo);
        int bookingSize = 4;

        // when
        bookingService.save(newBooking);

        // then
        assertEquals(bookingSize, bookingService.findAll().size());
    }

    @Test(expected = FailedInitializationException.class)
    public void createBookingUnsuccessfully() {
        //given
        LocalDate fourthFrom = LocalDate.of(2019, 12, 12);
        LocalDate fourthTo = LocalDate.of(2019, 12, 8);
        Booking newBooking = new Booking(4, 583, 6, 5, fourthFrom, fourthTo);

        // when and then
        assertThrows(FailedInitializationException.class, () -> bookingService.save(newBooking));
    }

    @Test
    public void findAll() {
        // given
        int sizeOfBookings = 3;

        // when and then
        assertEquals(sizeOfBookings, bookingService.findAll().size());
    }
}