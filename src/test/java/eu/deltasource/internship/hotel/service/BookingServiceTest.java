package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Booking;
import eu.deltasource.internship.hotel.domain.Gender;
import eu.deltasource.internship.hotel.domain.Guest;
import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.domain.commodity.*;
import eu.deltasource.internship.hotel.exception.ArgumentNotValidException;
import eu.deltasource.internship.hotel.exception.BookingOverlappingException;
import eu.deltasource.internship.hotel.domain.commodity.Bed;
import eu.deltasource.internship.hotel.domain.commodity.BedType;
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
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceTest {

	private BookingRepository bookingRepository = new BookingRepository();
	private GuestRepository guestRepository = new GuestRepository();
	private RoomRepository roomRepository = new RoomRepository();
	private RoomService roomService = new RoomService(roomRepository);
	private GuestService guestService = new GuestService(guestRepository);
	private BookingService bookingService = new BookingService(bookingRepository, roomService, guestService);
	private Booking firstBooking;
	private Booking secondBooking;

	@BeforeEach
	public void setUp() {
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

		// adds the rooms dto the repository which then can be accessed from  RoomService
		roomService.saveRooms(doubleRoom, singleRoom, kingSizeRoom);

		LocalDate firstFrom = LocalDate.of(2019, 8, 15);
		LocalDate firstTo = LocalDate.of(2019, 8, 18);
		firstBooking = new Booking(1, 1, 1, 2, firstFrom, firstTo);

		LocalDate secondFrom = LocalDate.of(2019, 9, 18);
		LocalDate secondTo = LocalDate.of(2019, 9, 21);
		secondBooking = new Booking(2, 2, 2, 1, secondFrom, secondTo);

		// adds the bookings dto the repository which then can be accessed from BookingService
		bookingService.saveAll(firstBooking, secondBooking);
	}

	@Test
	public void findByIdShouldWorkIfIdExisting() {
		assertEquals(firstBooking, bookingService.findById(firstBooking.getBookingId()));
	}

	@Test
	public void findByIdThatDoesNotExistShouldThrowException() {
		assertThrows(ItemNotFoundException.class, () -> bookingService.findById(bookingService.findAll().size() + 1));
	}

	@Test
	public void deleteByIdShouldSucceedIfIdIsValid() {
		assertTrue(bookingService.deleteById(secondBooking.getBookingId()));
		assertThrows(ItemNotFoundException.class, () -> bookingService.findById(secondBooking.getBookingId()));
	}

	@Test
	public void deleteByIdShouldFailIfIdDoesNotExist() {
		assertThrows(ItemNotFoundException.class, () -> bookingService.deleteById(bookingService.findAll().size() + 1));
	}

	@Test
	public void deleteBookingShouldWorkWhenBookingArgumentIsValid() {
		assertTrue(bookingService.delete(firstBooking));
		assertThrows(ItemNotFoundException.class, () -> bookingService.findById(firstBooking.getBookingId()));
		assertEquals(1, bookingService.findAll().size());
	}

	@Test
	public void deleteBookingThatDoesNotExistShouldThrowException() {
		//given
		// two bookings already exist
		LocalDate thirdFrom = LocalDate.of(2019, 8, 22);
		LocalDate thirdTo = LocalDate.of(2019, 8, 26);
		Booking thirdBooking = new Booking(3, 1, 1, 1, thirdFrom, thirdTo);

		//when and then
		assertThrows(ItemNotFoundException.class, () -> bookingService.delete(thirdBooking));
	}

	@Test
	public void deleteAllShouldEmptyTheRepository() {
		// given
		// two bookings already exist

		//when
		bookingService.deleteAll();

		//then
		assertFalse(bookingService.findAll().contains(firstBooking));
		assertFalse(bookingService.findAll().contains(secondBooking));
		assertTrue(bookingService.findAll().isEmpty());
	}

	@Test
	public void saveShouldCreateNewBookingIfNoOverlappingAndEnoughCapacity() {
		//given
		// two bookings already exist
		LocalDate thirdFrom = LocalDate.of(2019, 10, 3);
		LocalDate thirdTo = LocalDate.of(2019, 10, 8);
		Booking newBooking = new Booking
			(3, 1, 1, 2, thirdFrom, thirdTo);

		// when
		bookingService.save(newBooking);

		// then
		assertEquals(newBooking, bookingService.findById(newBooking.getBookingId()));
		assertEquals(3, bookingService.findAll().size());
		assertTrue(bookingService.findAll().contains(newBooking));
	}

	@Test
	public void saveShouldFailIfInvalidRoomOrOverlappingDates() {
		//given
		// two bookings already exist
		LocalDate thirdFrom = LocalDate.of(2019, 10, 3);
		LocalDate thirdTo = LocalDate.of(2019, 10, 8);
		Booking thirdBooking = new Booking
			(3, 3, roomService.findRooms().size() + 1, 2, thirdFrom, thirdTo);

		LocalDate from = LocalDate.of(2019, 8, 13);
		LocalDate to = LocalDate.of(2019, 8, 22);
		Booking fourthBooking = new Booking(1, 1, 1, 1, from, to);

		// when and then
		// overlapping
		assertThrows(BookingOverlappingException.class, () -> bookingService.save(fourthBooking));

		//invalid room id
		assertThrows(ItemNotFoundException.class,
			() -> bookingService.save(thirdBooking));

		// booking is null
		assertThrows(ArgumentNotValidException.class,
			() -> bookingService.save(null));
	}

	@Test
	public void saveAllShouldWorkIfNoConflicts() {
		//given
		// new bookings
		bookingService.deleteAll();
		LocalDate firstFrom = LocalDate.of(2019, 12, 3);
		LocalDate firstTo = LocalDate.of(2019, 12, 6);
		LocalDate secondFrom = LocalDate.of(2019, 12, 13);
		LocalDate secondTo = LocalDate.of(2019, 12, 16);
		Booking firstBooking = new Booking(1, 2, 3, 2, firstFrom, firstTo);
		Booking secondBooking = new Booking(2, 1, 2, 1, secondFrom, secondTo);

		//when
		bookingService.saveAll(firstBooking, secondBooking);

		//then
		assertEquals(2, bookingService.findAll().size());
		assertEquals(firstBooking, bookingService.findById(firstBooking.getBookingId()));
		assertEquals(secondBooking, bookingService.findById(secondBooking.getBookingId()));
	}

	@Test
	public void saveAllShouldThrowExceptionIfInvalidFields() {
		//given
		bookingService.deleteAll();
		LocalDate firstFrom = LocalDate.of(2019, 12, 3);
		LocalDate firstTo = LocalDate.of(2019, 12, 6);
		LocalDate secondFrom = LocalDate.of(2019, 12, 13);
		LocalDate secondTo = LocalDate.of(2019, 12, 16);
		Booking firstBooking = new Booking
			(1, 2, 3, 12, firstFrom, firstTo);
		Booking secondBooking = new Booking
			(2, 1, 7, 1, secondFrom, secondTo);

		//when and then
		//not enough capacity for the first booking and invalid room id for the second
		assertThrows(ArgumentNotValidException.class,
			() -> bookingService.saveAll(firstBooking, secondBooking));
	}

	@Test
	public void findAllExistingBookings() {
		assertThat("The repository does not contain expected number of bookings!",
			bookingService.findAll(), hasSize(2));
		assertThat("The repository does not contain all bookings!",
			bookingService.findAll(), containsInAnyOrder(firstBooking, secondBooking));
	}

	@Test
	public void updateBookingByRoomShouldWorkIfRoomVacantAndEnoughCapacity() {
		//given
		// from double to king size
		LocalDate from = LocalDate.of(2019, 8, 15);
		LocalDate to = LocalDate.of(2019, 8, 18);
		Booking booking = new Booking
			(1, 1, 3, 2, from, to);

		//when
		bookingService.updateBooking(booking);
		Booking updatedBooking = bookingService.findById(roomRepository.count());

		//then
		assertEquals(booking.getRoomId(), updatedBooking.getRoomId());
	}

	@Test
	public void updateBookingShouldThrowExceptionIfOverlapping() {
		//given
		// two bookings already exist
		//create a third booking for the second room
		LocalDate fromDate = LocalDate.of(2019, 8, 14);
		LocalDate toDate = LocalDate.of(2019, 8, 19);
		Booking thirdBooking = new Booking(3, 2, 2, 1, fromDate, toDate);
		bookingService.save(thirdBooking);

		LocalDate from = LocalDate.of(2019, 8, 15);
		LocalDate to = LocalDate.of(2019, 8, 18);
		Booking updatedBooking = new Booking(2, 2, 2, 1, from, to);

		//when and then
		// overlapping
		assertThrows(BookingOverlappingException.class,
			() -> bookingService.updateBooking(updatedBooking));
	}

	@Test
	public void updateBookingByNumOfPeopleShouldSucceedIfEnoughCapacity() {
		//given
		// two rooms already exist
		LocalDate from = LocalDate.of(2019, 8, 15);
		LocalDate to = LocalDate.of(2019, 8, 18);
		Booking booking = new Booking(1, 1, 1, 1, from, to);

		//when
		bookingService.updateBooking(booking);
		Booking updatedBooking = bookingService.findById(bookingRepository.count());

		//then
		assertEquals(booking.getNumberOfPeople(), updatedBooking.getNumberOfPeople());
	}

	@Test
	public void updateBookingByNumOfPeopleShouldFailIfNotEnoughRoom() {
		//given
		LocalDate from = LocalDate.of(2019, 8, 15);
		LocalDate to = LocalDate.of(2019, 8, 18);
		Booking booking = new Booking(1, 1, 1, 12, from, to);

		//when and then
		assertThrows(ArgumentNotValidException.class,
			() -> bookingService.updateBooking(booking));
	}

	@Test
	public void updateBookingShouldFailIfBookingDoesNotExistAlready() {
		//Given
		LocalDate from = LocalDate.of(2019, 8, 15);
		LocalDate to = LocalDate.of(2019, 8, 18);
		Booking updatedBooking = new Booking(bookingService.findAll().size() + 1, 1, 1, 2, from, to);

		//When

		//Then
		assertThrows(ItemNotFoundException.class, () -> bookingService.updateBooking(updatedBooking));
	}

	@Test
	public void updateBookingShouldFailIfWeTryToChangeGuestId() {
		//Given
		LocalDate from = LocalDate.of(2019, 8, 15);
		LocalDate to = LocalDate.of(2019, 8, 18);
		Booking updatedBooking = new Booking
			(1, bookingService.findById(1).getGuestId() + 1, 3, 2, from, to);

		//When

		//Then
		assertThrows(ArgumentNotValidException.class, () -> bookingService.updateBooking(updatedBooking));
	}

	@Test
	public void updateBookingByDatesShouldSucceedIfNotOverlapping() {
		// given
		// two bookings already exist
		LocalDate updateFrom = LocalDate.of(2019, 8, 24);
		LocalDate updateTo = LocalDate.of(2019, 8, 28);

		//when
		Booking findBooking = bookingService.updateBookingByDates(firstBooking.getBookingId(), updateFrom, updateTo);

		//then
		assertEquals(updateFrom, findBooking.getFrom());
		assertEquals(updateTo, findBooking.getTo());
	}

	@Test
	public void updateBookingByDatesShouldThrowExceptionIfOverlapping() {
		// given
		// two bookings already exist
		LocalDate from = LocalDate.of(2019, 8, 19);
		LocalDate to = LocalDate.of(2019, 8, 27);
		Booking booking = new Booking(1, 1, 1, 2, from, to);
		bookingService.save(booking);

		LocalDate updateFrom = LocalDate.of(2019, 8, 22);
		LocalDate updateTo = LocalDate.of(2019, 8, 26);

		//when and then
		assertThrows(BookingOverlappingException.class,
			() -> bookingService.updateBookingByDates(firstBooking.getBookingId(), updateFrom, updateTo));
	}

	@Test
	public void updateBookingByDatesShouldThrowExceptionIfDatesNotChronological() {
		assertThrows(ArgumentNotValidException.class, () -> bookingService.updateBookingByDates(1,
			LocalDate.of(2019, 9, 10), LocalDate.of(2019, 9, 2)));
	}

	@Test
	public void findAndBookFirstAvailableRoomShouldWorkIfNoConflicts() {
		//Given
		Booking thirdBooking = new Booking(3, 1, 1, 2,
			LocalDate.of(2019, 8, 18), LocalDate.of(2019, 8, 19));

		//When
		bookingService.findAndBookFirstAvailableRoom(thirdBooking);

		//Then
		assertTrue(bookingService.findAll().contains(thirdBooking));
		assertEquals(3, bookingService.findAll().size());
	}

	@Test
	public void findAndBookFirstAvailableRoomShouldFailIfOverlapping() {
		//Given
		Booking thirdBooking = new Booking(3, 1, 3, 2,
			LocalDate.of(2019, 8, 17), LocalDate.of(2019, 8, 23));
		bookingService.save(thirdBooking);
		Booking newBooking = new Booking(4, 1, 3, 2,
			LocalDate.of(2019, 8, 16), LocalDate.of(2019, 8, 21));

		//When

		//Then
		assertThrows(BookingOverlappingException.class, () -> bookingService.findAndBookFirstAvailableRoom(newBooking));
	}
}
