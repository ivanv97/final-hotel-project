package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Booking;
import eu.deltasource.internship.hotel.exception.BookingOverlappingException;
import eu.deltasource.internship.hotel.exception.FailedInitializationException;
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
	 * Searches booking by id
	 *
	 * @param id booking's id
	 * @return the found booking
	 */
	public Booking findById(int id) {
		if (bookingRepository.existsById(id)) {
			return bookingRepository.findById(id);
		}
		throw new ItemNotFoundException("Invalid id!");
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
		throw new ItemNotFoundException("Booking with such id does not exist!");
	}

	/**
	 * Deletes booking
	 *
	 * @param booking the booking that will be deleted
	 * @return true if the booking is successfully deleted
	 */
	public boolean delete(Booking booking) {
		bookingValidation(booking);
		findById(booking.getBookingId());
		return bookingRepository.delete(booking);
	}

	/**
	 * Deletes all bookings
	 */
	public void deleteAll() {
		if (bookingRepository.count() == 0) {
			throw new ItemNotFoundException("Empty list of bookings can not be deleted!");
		}
		bookingRepository.deleteAll();
	}

	/**
	 * Updates booking by either room id or number of people
	 *
	 * @param bookingId  id of the booking that will be updated
	 * @param newBooking the new booking
	 */
	public void updateBooking(int bookingId, Booking newBooking) {
		bookingValidation(newBooking);

		if (bookingRepository.existsById(bookingId)) {
			if (newBooking.getGuestId() != findById(bookingId).getGuestId()) {
				throw new FailedInitializationException("Invalid update!");
			}
			deleteById(bookingId);
			save(newBooking);
		} else {
			throw new ItemNotFoundException("Invalid booking Id");
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

		dateValidation(from, to);

		Booking booking = findById(bookingId);

		if (checkDatesOverlapping(from, to, booking.getRoomId(), bookingId)) {
			return bookingRepository.updateDates(booking);
		}
		throw new BookingOverlappingException("Overlapping dates!");
	}

	/**
	 * @return list of all bookings
	 */
	public List<Booking> findAll() {
		if (bookingRepository.count() == 0) {
			throw new ItemNotFoundException("Empty list!");
		}
		return bookingRepository.findAll();
	}

	/**
	 * Creates new booking
	 *
	 * @param newBooking the new booking
	 */
	public void save(Booking newBooking) {
		bookingValidation(newBooking);
		bookingOverlapping(newBooking.getFrom(), newBooking.getTo(), newBooking.getRoomId());
		bookingRepository.save(newBooking);
	}

	/**
	 * Creates array of bookings
	 *
	 * @param bookings array of bookings
	 */
	public void saveAll(Booking... bookings) {
		if (bookings == null || bookings.length == 0) {
			throw new FailedInitializationException("Empty array of bookings");
		}
		for (Booking booking : bookings) {
			save(booking);
		}
	}

	private boolean checkDatesOverlapping(LocalDate from, LocalDate to, int roomId, int bookingId, int... idToIgnore) {
		if (findAll().isEmpty()) {
			return true;
		}
		for (Booking booking : findAll()) {
			if (booking.getRoomId() == roomId) {
				if (idToIgnore.length > 0 && booking.getBookingId() == idToIgnore[0]) {
					continue;
				}
				if (bookingId == booking.getBookingId()) {
					return (checkDatesOverlapping(from, to, roomId, bookingId, bookingId));
				}
				if (!from.isBefore(booking.getTo()) || !to.isAfter(booking.getFrom())) {
					continue;
				}
				return false;
			}
		}
		return true;
	}

	private void bookingValidation(Booking booking) {
		if (booking == null || !checkBookingFields(booking)) {
			throw new FailedInitializationException("Invalid Booking!");
		}
	}

	private boolean checkBookingFields(Booking booking) {
		int roomId = booking.getRoomId(), numberOfPeople = booking.getNumberOfPeople(), guestId = booking.getGuestId();
		LocalDate from = booking.getFrom();
		LocalDate to = booking.getTo();

		dateValidation(from, to);

		if (roomService.getRoomById(roomId).getRoomId() == roomId &&
			roomService.getRoomById(roomId).getRoomCapacity() >= numberOfPeople &&
			guestService.findById(guestId).getGuestId() == guestId) {
			return true;
		}
		return false;
	}

	private void dateValidation(LocalDate from, LocalDate to) {
		if (from == null || to == null || from.isAfter(to) || from.equals(to) || from.isBefore(LocalDate.now())) {
			throw new FailedInitializationException("Invalid dates!");
		}
	}

	private void bookingOverlapping(LocalDate from, LocalDate to, int roomId) {
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
					throw new BookingOverlappingException("Overlapping");
				}
			}
		}
	}
}
