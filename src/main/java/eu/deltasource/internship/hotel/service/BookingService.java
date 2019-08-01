package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Booking;
import eu.deltasource.internship.hotel.exception.BookingOverlappingException;
import eu.deltasource.internship.hotel.exception.FailedInitializationException;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.Period;
import java.util.List;

/**
 * Represents booking services
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
		throw new ItemNotFoundException("Invalid id");
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
	 * Updates existing booking by dates
	 *
	 * @param bookingId booking's ID
	 * @param from      starting date
	 * @param to        ending date
	 * @return the new booking
	 */
	public Booking updateBooking(int bookingId, LocalDate from, LocalDate to) {

		if (!dateValidation(from, to)) {
			throw new FailedInitializationException("Invalid dates!");
		}
		Booking booking = bookingRepository.findById(bookingId);

		if (!bookingOverlappingValidation(from, to, booking)) {
			booking.setBookingDates(from, to);
			return bookingRepository.updateDates(booking);
		}
		throw new BookingOverlappingException("Invalid Booking!");
	}


	/**
	 * Updates booking by Id
	 *
	 * @param bookingId  search room id by booking id
	 * @param newBooking the new booking
	 */
	public void updateBookingByRoomId(int bookingId, Booking newBooking) {
		if (!bookingValidation(newBooking)) {
			throw new FailedInitializationException("Invalid Booking!");
		}

		int searchedRoomId = roomService.getRoomById(newBooking.getRoomId()).getRoomId();
		int currentBookingRoomID = bookingRepository.findById(bookingId).getRoomId();

		if (bookingRepository.existsById(bookingId) && (currentBookingRoomID == searchedRoomId)) {
			deleteById(bookingId);
			bookingRepository.save(newBooking);
		}
		throw new ItemNotFoundException("Invalid Id");
	}

	public void updateBookingByRoomId(int bookingId, Booking newBooking) {
		if (!bookingValidation(newBooking)) {
			throw new FailedInitializationException("Invalid Booking!");
		}

		int searchedRoomId = roomService.getRoomById(newBooking.getRoomId()).getRoomId();
		int currentBookingRoomID = bookingRepository.findById(bookingId).getRoomId();

		if (bookingRepository.existsById(bookingId) && (currentBookingRoomID == searchedRoomId)) {
			deleteById(bookingId);
			bookingRepository.save(newBooking);
		}
		throw new ItemNotFoundException("Invalid Id");
	}

	/**
	 * Checks if the new date overlaps the room's booking
	 *
	 * @param from        starting date
	 * @param to          ending date
	 * @param roomBooking room's booking
	 * @return
	 */
	public boolean checkOverlapping(LocalDate from, LocalDate to, Booking roomBooking) {

		if (from.isAfter(roomBooking.getFrom()) && to.isAfter(roomBooking.getTo())) {
			return false;
		}

		Period period = Period.between(from, to);
		int totalDays = period.getDays();

		if (from.plusDays(totalDays).isBefore(roomBooking.getFrom())
			|| from.plusDays(totalDays).isEqual(roomBooking.getFrom())) {
			return false;
		}
		return true;
	}

	/**
	 * Finds all bookings
	 *
	 * @return list of all bookings
	 */
	public List<Booking> findAll() {
		return bookingRepository.findAll();
	}

	/**
	 * Creates new booking
	 *
	 * @param newBooking the new booking
	 */
	public void save(Booking newBooking) {
		if (!bookingValidation(newBooking)) {
			throw new FailedInitializationException("Invalid booking!");
		}
		bookingRepository.save(newBooking);
	}

	/**
	 * Creates array of bookings
	 *
	 * @param bookings array of bookings
	 */
	public void saveAll(Booking... bookings) {
		if (!bookingValidation(bookings)) {
			throw new FailedInitializationException("Invalid bookings!");
		}
		bookingRepository.saveAll(bookings);
	}

	private boolean dateValidation(LocalDate from, LocalDate to) {
		if (from.isAfter(to) || from.equals(to) || from == null || to == null)
			return false;
		return true;
	}

	private boolean bookingOverlappingValidation(LocalDate from, LocalDate to, Booking booking) {
		int roomID = booking.getRoomId();

		for (Booking book : bookingRepository.findAll()) {
			if (roomID == book.getRoomId() && checkOverlapping(from, to, book)) {
				return true;
			}
		}
		return false;
	}

	private boolean bookingValidation(Booking... booking) {
		if (booking == null)
			return false;
		return true;
	}
}
