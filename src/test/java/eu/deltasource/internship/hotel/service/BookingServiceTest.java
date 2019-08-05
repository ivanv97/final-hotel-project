package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Booking;
import eu.deltasource.internship.hotel.domain.Gender;
import eu.deltasource.internship.hotel.domain.Guest;
import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.domain.commodity.*;
import eu.deltasource.internship.hotel.exception.BookingOverlappingException;
import eu.deltasource.internship.hotel.exception.FailedInitializationException;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.BookingRepository;
import eu.deltasource.internship.hotel.repository.GuestRepository;
import eu.deltasource.internship.hotel.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static eu.deltasource.internship.hotel.domain.commodity.BedType.SINGLE;
import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceTest {

    BookingRepository bookingRepository = new BookingRepository();
    GuestRepository guestRepository = new GuestRepository();
    RoomRepository roomRepository = new RoomRepository();
    RoomService roomService = new RoomService(roomRepository);
    GuestService guestService = new GuestService(guestRepository);
    BookingService bookingService = new BookingService(bookingRepository, roomService, guestService);
    Booking firstBooking;
    Booking secondBooking;

    @BeforeEach
    void setUp() {

        // guests
        Guest firstGuest = new Guest(1, "John", "Miller", Gender.MALE);
        Guest secondGuest = new Guest(2, "Maria", "Tam", Gender.FEMALE);
        guestService.saveAll(firstGuest, secondGuest);

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

        LocalDate firstFrom = LocalDate.of(2019, 8, 15);
        LocalDate firstTo = LocalDate.of(2019, 8, 18);
        firstBooking = new Booking(1, 1, 1,
                2, firstFrom, firstTo);

        LocalDate secondFrom = LocalDate.of(2019, 9, 18);
        LocalDate secondTo = LocalDate.of(2019, 9, 21);
        secondBooking = new Booking(2, 2, 2,
                1, secondFrom, secondTo);

        // adds the bookings to the repository which then can be accessed from BookingService
        bookingService.saveAll(firstBooking, secondBooking);
    }

    @Test
    public void findBookingByExistingId() {
        //given
        // two bookings already exist
        int bookingId = 1;

        //when
        Booking booking = bookingService.findById(bookingId);

        // then
        assertTrue(bookingService.findById(bookingId).equals(booking));
    }

    @Test
    public void findBookingByIdThatDoesNotExist() {
        //given
        // two bookings already exist
        int bookingId = -5;

        //when and then
        assertThrows(ItemNotFoundException.class, () -> bookingService.findById(bookingId));
    }

    @Test
    public void deleteByExistingId() {
        //given
        // two bookings already exist
        int bookingID = 2;

        //when
        boolean result = bookingService.deleteById(bookingID);

        // then
        assertEquals(true, result);
        assertThrows(ItemNotFoundException.class, () -> bookingService.findById(bookingID));
    }

    @Test
    public void deleteByIDThatDoesNotExist() {
        //given
        // two bookings already exist
        int bookingID = 56;

        //when and then
        assertThrows(ItemNotFoundException.class, () -> bookingService.deleteById(bookingID));
    }

    @Test
    public void deleteExistingBooking() {
        //given
        // two bookings already exist
        int bookingId = firstBooking.getBookingId();
        int size = 1;

        //when
        boolean result = bookingService.delete(firstBooking);

        assertEquals(true, result);
        assertThrows(ItemNotFoundException.class, () -> bookingService.findById(bookingId));
        assertTrue(size == bookingService.findAll().size());
    }

    @Test
    public void deleteBookingThatDoesNotExist() {
        //given
        // two bookings already exist
        int guestId = 1, bookingId = 3, roomId = 1, numberOfPeople = 1;
        LocalDate thirdFrom = LocalDate.of(2019, 8, 22);
        LocalDate thirdTo = LocalDate.of(2019, 8, 26);
        Booking thirdBooking = new Booking(bookingId, guestId,
                roomId, numberOfPeople, thirdFrom, thirdTo);

        //when and then
        assertThrows(ItemNotFoundException.class, () -> bookingService.delete(thirdBooking));
    }

    @Test
    public void deleteAllExistingBookings() {
        // given
        // two bookings already exist
        int firstBookingId = 1, secondBookingId = 2;

        //when
        bookingService.deleteAll();

        //then
        assertThrows(ItemNotFoundException.class, () -> bookingService.findById(firstBookingId));
        assertThrows(ItemNotFoundException.class, () -> bookingService.findById(secondBookingId));
    }

    @Test
    public void deleteAllBookingsThatDoNotExist() {
        // given
        BookingRepository bookingRepository = new BookingRepository();
        BookingService bookingService = new BookingService(bookingRepository, roomService, guestService);

        //when and then
        assertThrows(ItemNotFoundException.class, () -> bookingService.findAll());
    }

    @Test
    public void createBookingSuccessfully() {
        //given
        // two bookings already exist
        int bookingId = 3, guestId = 1, roomId = 1, numberOfPeople = 2;
        LocalDate thirdFrom = LocalDate.of(2019, 10, 3);
        LocalDate thirdTo = LocalDate.of(2019, 10, 8);
        Booking newBooking = new Booking
                (bookingId, guestId, roomId, numberOfPeople, thirdFrom, thirdTo);
        int expectedBookingSize = 3;

        // when
        bookingService.save(newBooking);

        // then
        assertTrue(bookingService.findById(bookingId).equals(newBooking));
        assertEquals(expectedBookingSize, bookingService.findAll().size());
    }

    @Test
    public void createBookingUnsuccessfully() {
        //given
        // two bookings already exist
        int bookingId = 3, guestId = 3, roomId = 7, numberOfPeople = 2;
        LocalDate thirdFrom = LocalDate.of(2019, 10, 3);
        LocalDate thirdTo = LocalDate.of(2019, 10, 8);
        Booking thirdBooking = new Booking
                (bookingId, guestId, roomId, numberOfPeople, thirdFrom, thirdTo);

        LocalDate from = LocalDate.of(2019, 8, 12);
        LocalDate to = LocalDate.of(2019, 8, 17);
        Booking fourthBooking = new Booking(1, 1, 1, 1, from, to);

        // when and then
        // overlapping
        assertThrows(BookingOverlappingException.class, () -> bookingService.save(fourthBooking));

        //invalid room id
        assertThrows(ItemNotFoundException.class,
                () -> bookingService.save(thirdBooking));

        assertThrows(FailedInitializationException.class,
                () -> bookingService.save(null));
    }

    @Test
    public void saveAllBookingSuccessfully() {
        //given
        BookingRepository bookingRepository = new BookingRepository();
        BookingService bookingService = new BookingService(bookingRepository, roomService, guestService);
        int firstBookingId = 1, firstGuestId = 2, firstRoomId = 3, firstNumOfPeople = 2;
        int secondBookingId = 2, secondGuestId = 1, secondRoomId = 2, secondNumOfPeople = 1;
        int expectedSize = 2;
        LocalDate firstFrom = LocalDate.of(2019, 12, 3);
        LocalDate firstTo = LocalDate.of(2019, 12, 6);
        LocalDate secondFrom = LocalDate.of(2019, 12, 13);
        LocalDate secondTo = LocalDate.of(2019, 12, 16);
        Booking firstBooking = new Booking
                (firstBookingId, firstGuestId, firstRoomId, firstNumOfPeople, firstFrom, firstTo);
        Booking secondBooking = new Booking
                (secondBookingId, secondGuestId, secondRoomId, secondNumOfPeople, secondFrom, secondTo);

        //when
        bookingService.saveAll(firstBooking, secondBooking);

        //then
        assertEquals(expectedSize, bookingService.findAll().size());
        assertTrue(bookingService.findById(firstBookingId).equals(firstBooking));
        assertTrue(bookingService.findById(secondBookingId).equals(secondBooking));
    }

    @Test
    public void saveAllBookingUnsuccessfully() {
        //given
        BookingRepository bookingRepository = new BookingRepository();
        BookingService bookingService = new BookingService(bookingRepository, roomService, guestService);
        int firstBookingId = 1, firstGuestId = 2, firstRoomId = 3, firstNumOfPeople = 12;
        int secondBookingId = 2, secondGuestId = 1, secondRoomId = 7, secondNumOfPeople = 1;
        LocalDate firstFrom = LocalDate.of(2019, 12, 3);
        LocalDate firstTo = LocalDate.of(2019, 12, 6);
        LocalDate secondFrom = LocalDate.of(2019, 12, 13);
        LocalDate secondTo = LocalDate.of(2019, 12, 16);
        Booking firstBooking = new Booking
                (firstBookingId, firstGuestId, firstRoomId, firstNumOfPeople, firstFrom, firstTo);
        Booking secondBooking = new Booking
                (secondBookingId, secondGuestId, secondRoomId, secondNumOfPeople, secondFrom, secondTo);

        //when and then
        //not enough capacity for the first booking and invalid room id for the second
        assertThrows(FailedInitializationException.class,
                () -> bookingService.saveAll(firstBooking, secondBooking));
    }

    @Test
    public void findAllExistingBookings() {
        // given
        //two bookings already exist
        int expectedSize = 2;

        //when
        int actualSize = bookingService.findAll().size();

        assertEquals(expectedSize, actualSize);
        assertTrue(bookingService.findAll().contains(firstBooking));
        assertTrue(bookingService.findAll().contains(secondBooking));
    }

    @Test
    public void updateBookingByExistingRoomId() {
        //given
        // from double to king size
        int numberOfPeople = 2, roomId = 3, bookingId = 1, guestId = 1;
        LocalDate from = LocalDate.of(2019, 8, 15);
        LocalDate to = LocalDate.of(2019, 8, 18);
        Booking booking = new Booking
                (bookingId, guestId, roomId, numberOfPeople, from, to);

        //when
        bookingService.updateBooking(bookingId, booking);
        Booking updatedBooking = bookingService.findById(roomRepository.count());

        //then
        assertTrue(updatedBooking.getRoomId() == booking.getRoomId());
    }

    @Test
    public void updateBookingByRoomIdThatDoesNotExist() {
        //given
        // from double to king size
        int numberOfPeople = 2, roomId = 7, bookingId = 1, guestId = 1;
        LocalDate from = LocalDate.of(2019, 8, 15);
        LocalDate to = LocalDate.of(2019, 8, 18);
        Booking booking = new Booking
                (bookingId, guestId, roomId, numberOfPeople, from, to);

        //when and then
        assertThrows(ItemNotFoundException.class,
                () -> bookingService.updateBooking(bookingId, booking));
    }

    @Test
    public void updateBookingByNumOfPeopleSuccessfully() {
        //given
        // two rooms already exist
        int bookingId = 1, guestId = 1, numOfPeople = 1, roomId = 1;
        LocalDate from = LocalDate.of(2019, 8, 15);
        LocalDate to = LocalDate.of(2019, 8, 18);
        Booking booking = new Booking(bookingId, guestId, roomId, numOfPeople, from, to);

        //when
        bookingService.updateBooking(bookingId, booking);
        Booking updatedBooking = bookingService.findById(bookingRepository.count());

        //then
        assertTrue(updatedBooking.getNumberOfPeople() == booking.getNumberOfPeople());
    }

    @Test
    public void updateBookingByNumOfPeopleUnsuccessfully() {
        //given
        int bookingId = 1, guestId = 1, numOfPeople = 12, roomId = 1;
        LocalDate from = LocalDate.of(2019, 8, 15);
        LocalDate to = LocalDate.of(2019, 8, 18);
        Booking booking = new Booking(bookingId, guestId, roomId, numOfPeople, from, to);

        //when and then
        assertThrows(FailedInitializationException.class,
                () -> bookingService.updateBooking(bookingId, booking));
    }

    @Test
    public void updateBookingSuccessfully() {
        // given
        // two bookings already exist
        int bookingId = 1;
        LocalDate updateFrom = LocalDate.of(2019, 8, 24);
        LocalDate updateTo = LocalDate.of(2019, 8, 28);

        //when
        Booking findBooking = bookingService.updateBookingByDates(bookingId, updateFrom, updateTo);

        assertTrue(bookingService.findById(bookingId).getFrom().equals(findBooking.getFrom()));
        assertTrue(bookingService.findById(bookingId).getTo().equals(findBooking.getTo()));
    }

    @Test
    public void updateBookingUnsuccessfully() {
        // given
        // two bookings already exist
        int bookingId = 1, guestId = 1, numOfPeople = 2, roomId = 1;
        LocalDate from = LocalDate.of(2019, 8, 19);
        LocalDate to = LocalDate.of(2019, 8, 27);
        Booking booking = new Booking(bookingId, guestId, roomId, numOfPeople, from, to);
        bookingService.save(booking);

        LocalDate updateFrom = LocalDate.of(2019, 8, 17);
        LocalDate updateTo = LocalDate.of(2019, 8, 24);

        //when and then
        assertThrows(BookingOverlappingException.class,
                () -> bookingService.updateBookingByDates(bookingId, updateFrom, updateTo));
    }
}