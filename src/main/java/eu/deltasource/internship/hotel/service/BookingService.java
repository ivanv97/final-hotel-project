package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Booking;
import eu.deltasource.internship.hotel.exception.BookingOverlappingException;
import eu.deltasource.internship.hotel.exception.FailedInitializationException;
import eu.deltasource.internship.hotel.exception.ArgumentNotValidException;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents services for bookings
 */
@Service
public class BookingService {

	private final BookingRepository bookingRepository;
	private final RoomService roomService;
	private final GuestService guestService;

	@Autowired
	public BookingService(BookingRepository bookingRepository, RoomService roomService, GuestService guestService) {
		this.bookingRepository = bookingRepository;
		this.roomService = roomService;
		this.guestService = guestService;
	}

	/**
	 * Deletes booking by id
	 *
	 * @param id booking's id
	 */
	public boolean deleteById(int id) {
		if (bookingRepository.existsById(id)) {
			return bookingRepository.deleteById(id);
		}
		throw new ItemNotFoundException("Booking with id " + id + " does not exist!");
	}

	/**
	 * Deletes booking
	 *
	 * @param booking the booking that will be deleted
	 * @return true if the booking is successfully deleted
	 */
	public boolean delete(Booking booking) {
		validBooking(booking);
		return bookingRepository.delete(findById(booking.getBookingId()));
	}

	/**
	 * Updates booking by either room id or number of people
	 *
	 * @param bookingId  id of the booking that will be updated
	 * @param newBooking the new booking
	 */
	public void updateBooking(int bookingId, Booking newBooking) {
		if (bookingRepository.existsById(bookingId)) {
			validBooking(newBooking);

			int roomId = newBooking.getRoomId(), guestId = newBooking.getGuestId(), numOfPeople = newBooking.getNumberOfPeople();
			LocalDate from = newBooking.getFrom(), to = newBooking.getTo();

			validUpdateBooking(newBooking, bookingId);
			deleteById(bookingId);
			save(new Booking(bookingId, guestId, roomId, numOfPeople, from, to));
		} else {
			throw new ItemNotFoundException("Booking with id " + bookingId + " does not exist!");
		}
	}

	/**
	 * Updates booking by dates
	 *
	 * @param bookingId booking's id that will be updated
	 * @param from      starting date
	 * @param to        ending date
	 * @return updated booking
	 */
	public Booking updateBookingByDates(int bookingId, LocalDate from, LocalDate to) {
		validDates(from, to);
		Booking booking = findById(bookingId);

		if (validUpdateDatesOverlapping(from, to, booking.getRoomId(), bookingId)) {
			booking.setBookingDates(from, to);
			return bookingRepository.updateDates(booking);
		}
		throw new BookingOverlappingException("Overlapping dates!");
	}

	/**
	 * Creates new booking
	 *
	 * @param newBooking the new booking
	 */
	public void save(Booking newBooking) {
		validBooking(newBooking);
		bookingOverlappingCreateValidation(newBooking.getFrom(), newBooking.getTo(), newBooking.getRoomId());
		bookingRepository.save(newBooking);
	}

	/**
	 * Creates array of bookings
	 *
	 * @param bookings array of bookings
	 */
	public void saveAll(Booking... bookings) {
		if (bookings == null) {
			throw new FailedInitializationException("Empty array of bookings");
		}
		for (Booking booking : bookings) {
			save(booking);
		}
	}

	private void validUpdateBooking(Booking booking, int bookingId) {
		if (booking.getGuestId() != findById(bookingId).getGuestId()) {
			throw new FailedInitializationException("You are not allowed to change guest id!");
		}
		if (!validUpdateDatesOverlapping(booking.getFrom(), booking.getTo(), booking.getRoomId(), bookingId, bookingId)) {
			throw new BookingOverlappingException("The room is already booked for this period!");
		}
	}

	private boolean validUpdateDatesOverlapping(LocalDate from, LocalDate to, int roomId, int bookingId, int... idToIgnore) {
		if (findAll().isEmpty()) {
			return true;
		}
		for (Booking booking : findAll()) {
			if (booking.getRoomId() == roomId) {
				if (idToIgnore.length > 0 && booking.getBookingId() == idToIgnore[0]) {
					continue;
				}
				if (bookingId == booking.getBookingId()) {
					return (validUpdateDatesOverlapping(from, to, roomId, bookingId, bookingId));
				}
				if (!from.isBefore(booking.getTo()) || !to.isAfter(booking.getFrom())) {
					continue;
				}
				return false;
			}
		}
		return true;
	}


	private void validBooking(Booking booking) {
		if (booking == null || !validBookingFields(booking)) {
			throw new FailedInitializationException("Invalid Booking!");
		}
	}

	private boolean validBookingFields(Booking booking) {
		int roomId = booking.getRoomId(), numberOfPeople = booking.getNumberOfPeople(), guestId = booking.getGuestId();
		LocalDate from = booking.getFrom(), to = booking.getTo();

		validDates(from, to);

		if (roomService.getRoomById(roomId).getRoomId() == roomId &&
			roomService.getRoomById(roomId).getRoomCapacity() >= numberOfPeople &&
			guestService.findById(guestId).getGuestId() == guestId) {
			return true;
		}
		return false;
	}

	private void validDates(LocalDate from, LocalDate to) {
		if (from == null || to == null || from.isAfter(to) || from.equals(to) || from.isBefore(LocalDate.now())) {
			throw new FailedInitializationException("Invalid dates!");
		}
	}

	private void bookingOverlappingCreateValidation(LocalDate from, LocalDate to, int roomId) {
		if (bookingRepository.count() == 0) {
			return;
		}

		for (Booking book : findAll()) {
			if (book.getRoomId() == roomId) {
				if ((from.isBefore(book.getFrom()) && to.equals(book.getFrom()) ||

					(from.isBefore(book.getFrom()) && (to.isBefore(book.getFrom()))))) {
					continue;
				}
				if ((book.getFrom().equals(from) || book.getTo().equals(to)) ||

					(from.isBefore(book.getFrom()) && to.isAfter(book.getTo())) ||

					(from.isAfter(book.getFrom()) && to.isBefore(book.getTo())) ||

					(to.isBefore(book.getTo()))) {
					throw new BookingOverlappingException("The booking can not be created because dates are overlapped!");
				}
			}
		}
	}

	/**
	 * Returns a list of
	 * all bookings for all rooms
	 */
	public List<Booking> findAll() {
		return bookingRepository.findAll();
	}

	/**
	 * Gets a booking by its ID
	 *
	 * @param id to be searched by
	 * @return Booking object that matches the given id
	 * @throws ItemNotFoundException If the Id doesn't exist in the database
	 */
	public Booking findById(int id) {
		if (!bookingRepository.existsById(id)) {
			throw new ItemNotFoundException("There are no bookings with that ID!");
		}
		return bookingRepository.findById(id);
	}


	/**
	 * Saves a list of booking objects
	 * Checks each one separately beforehand
	 *
	 * @param items The list we want to save
	 * @throws ArgumentNotValidException If any of the booking
	 *                                   objects is not valid
	 */
	public void saveAll(List<Booking> items) {
		for (Booking item : items) {
			bookingRepository.save(item);
		}
	}

	/**
	 * Deletes every single booking
	 * from the repo
	 */
	public void deleteAll() {
		bookingRepository.deleteAll();
	}
}

