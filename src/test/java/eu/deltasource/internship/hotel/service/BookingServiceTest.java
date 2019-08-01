package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Booking;
import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.domain.commodity.*;
import eu.deltasource.internship.hotel.exception.BookingOverlappingException;
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
    RoomService roomService = new RoomService(roomRepository);
    GuestService guestService = new GuestService(guestRepository);
    BookingService bookingService = new BookingService(bookingRepository, roomService, guestService);

    @Before
    public void setUp() {
        // Commodities for a double room
        Set<AbstractCommodity> doubleSet = new HashSet<>(Arrays.asList
                (new Bed(BedType.DOUBLE), new Toilet(), new Shower()));

        // commodities for a single room
        Set<AbstractCommodity> singleSet = new HashSet<>(Arrays.asList
                (new Bed(SINGLE), new Toilet(), new Shower()));

        // commodities for a double room with king size bed
        Set<AbstractCommodity> kingSizeSet = new HashSet<>(Arrays.asList
                (new Bed(BedType.KING_SIZE), new Toilet(), new Shower()));

        // create some rooms
        Room doubleRoom = new Room(1, doubleSet);
        Room singleRoom = new Room(2, singleSet);
        Room kingSizeRoom = new Room(3, kingSizeSet);

        // adds the rooms to the repository which then can be accessed from  RoomService
        roomService.saveRooms(doubleRoom, singleRoom, kingSizeRoom);

        /*
        LocalDate firstFrom = LocalDate.of(2019, 8, 15);
        LocalDate firstTo = LocalDate.of(2019, 8, 18);
        Booking firstBooking = new Booking(1, 101, 1,
                3, firstFrom, firstTo);

        LocalDate secondFrom = LocalDate.of(2019, 9, 18);
        LocalDate secondTo = LocalDate.of(2019, 9, 21);
        Booking secondBooking = new Booking(12, 345, 4,
                3, secondFrom, secondTo);

        // adds the bookings to the repository which then can be accessed from BookingService
        bookingService.saveAll(firstBooking, secondBooking);

         */
    }

    @Test
    public void findByID() {
        //given
        LocalDate thirdFrom = LocalDate.of(2019, 9, 4);
        LocalDate thirdTo = LocalDate.of(2019, 9, 7);
        Booking thirdBooking = new Booking(3, 465, 4,
                3, thirdFrom, thirdTo);
        bookingService.save(thirdBooking);
        int bookingID = 3;

        //when
        Booking booking = bookingService.findById(bookingID);

        // then
        assertTrue(bookingID == booking.getBookingId());
    }

    @Test
    public void notFoundID() {
        // given
        int bookID = -5;

        //when and then
        assertThrows(ItemNotFoundException.class, () -> bookingService.findById(bookID));
    }

    @Test
    public void deleteByExistingID() {
        //given
        int bookingID = 2;

        //when
        boolean result = bookingService.deleteById(bookingID);

        // then
        assertEquals(true, result);
    }

    @Test
    public void deleteByIDThatDoesNotExists() {
        //given
        int bookingID = 56;

        //when and then
        assertThrows(ItemNotFoundException.class, () -> bookingService.deleteById(bookingID));
    }

    @Test
    public void updateDatesSuccessfully() {
        // given
        int bookingID = 1;
        LocalDate fromDate = LocalDate.of(2019, 8, 7);
        LocalDate toDate = LocalDate.of(2019, 8, 12);

        // when and then
        assertDoesNotThrow(() -> bookingService.updateBooking(bookingID, fromDate, toDate));
    }


    @Test
    public void updateDatesTest() {
        // given
    /*    int bookingID = 1;
        LocalDate secondFrom = LocalDate.of(2019, 9, 1);
        LocalDate secondTo = LocalDate.of(2019, 9, 5);
        Booking secondBooking = new Booking(bookingID, 345, 4,
                3, secondFrom, secondTo);
        bookingService.save(secondBooking);

        LocalDate from = LocalDate.of(2019, 9, 2);
        LocalDate to = LocalDate.of(2019, 9, 4);

        // when and then 1 /Efrem/

        assertThrows(BookingOverlappingException.class,
                () -> bookingService.updateBooking(bookingID, from, to));
*/
        int bookingID = 1;
        LocalDate secondFrom = LocalDate.of(2019, 9, 11);
        LocalDate secondTo = LocalDate.of(2019, 9, 15);
        Booking secondBooking = new Booking(bookingID, 345, 4,
                3, secondFrom, secondTo);
        bookingService.save(secondBooking);

        LocalDate from = LocalDate.of(2019, 9, 1);
        LocalDate to = LocalDate.of(2019, 9, 5);

        // when /Taner/
        Booking booking = bookingService.updateBooking(bookingID, from, to);

        // then /Taner/
        assertTrue(booking.equals(secondBooking));
    }

    @Test
    public void updateDatesUnsuccessfullyOverlapping() {
        // given
        int bookingID = 1;
        LocalDate fromDate = LocalDate.of(2019, 8, 15);
        LocalDate toDate = LocalDate.of(2019, 8, 18);

        // when and then 1
        assertThrows(BookingOverlappingException.class,
                () -> bookingService.updateBooking(bookingID, fromDate, toDate));

        // when and then 2
        LocalDate from = LocalDate.of(2019, 8, 3);
        LocalDate to = LocalDate.of(2019, 8, 17);

        assertThrows(BookingOverlappingException.class,
                () -> bookingService.updateBooking(bookingID, from, to));
    }

    @Test
    public void createBookingSuccessfully() {
        //given
        LocalDate from = LocalDate.of(2019, 10, 3);
        LocalDate to = LocalDate.of(2019, 10, 8);
        Booking newBooking = new Booking(4, 583, 6, 5, from, to);
        int bookingSize = 3;

        // when
        bookingService.save(newBooking);

        // then
        assertEquals(bookingSize, bookingService.findAll().size());
    }

    @Test
    public void createBookingsNullCheck() {
        // given
        Booking[] bookings = null;

        assertThrows(FailedInitializationException.class, () -> bookingService.saveAll(bookings));
    }

    @Test(expected = FailedInitializationException.class)
    public void createBookingUnsuccessfully() {
        //given
        LocalDate from = LocalDate.of(2019, 12, 12);
        LocalDate to = LocalDate.of(2019, 12, 8);
        Booking newBooking = new Booking(4, 583, 6, 5, from, to);

        // when and then
        assertThrows(FailedInitializationException.class, () -> bookingService.save(newBooking));
    }

    @Test
    public void saveNullCheck() {
        //given
        Booking booking = null;

        //when and then
        assertThrows(FailedInitializationException.class, () -> bookingService.save(booking));
    }

    @Test(expected = FailedInitializationException.class)
    public void updateBookingInvalidDates() {
        //given
        LocalDate fourthFrom = LocalDate.of(2019, 10, 12);
        LocalDate fourthTo = LocalDate.of(2019, 10, 4);
        Booking newBooking = new Booking(4, 583, 6,
                5, fourthFrom, fourthTo);

        // when and then
        assertThrows(FailedInitializationException.class, () -> bookingService.save(newBooking));
    }

    @Test
    public void findAll() {
        // given
        int sizeOfBookings = 2;

        // when and then
        assertEquals(sizeOfBookings, bookingService.findAll().size());
    }
}