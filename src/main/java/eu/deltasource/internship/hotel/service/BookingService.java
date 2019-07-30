package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Booking;
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
	 * Searches booking by ID
	 *
	 * @param ID booking's ID
	 * @return the found booking
	 */
	public Booking findByID(int ID) {
		return bookingRepository.findById(ID);
	}

	/**
	 * Deletes booking by ID
	 *
	 * @param ID booking's ID
	 */
	public boolean deleteByID(int ID) {
		if (bookingRepository.deleteById(ID)) {
			return true;
		}
		throw new ItemNotFoundException("Booking with such ID does not exits!");
	}

	/**
	 * Updates existing booking
	 *
	 * @param bookingID booking's ID
	 * @param from      starting date
	 * @param to        ending date
	 */
	public void updateBooking(int bookingID, LocalDate from, LocalDate to) {
		if (from == null || to == null || from.isAfter(to) || from.equals(to)) {
			throw new FailedInitializationException("Invalid dates!");
		}

		Booking booking = bookingRepository.findById(bookingID);
		int roomID = booking.getRoomId();

		for (Booking book : bookingRepository.findAll()) {
			if (roomID == book.getRoomId() && checkOverlapping(from, to, book)) {
				throw new FailedInitializationException("Dates are overlapped!");
			}
		}
		booking.setBookingDates(from, to);
		bookingRepository.save(booking);
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
		// not interested
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
		if (newBooking == null) {
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
		if (bookings == null) {
			throw new FailedInitializationException("Invalid booking!");
		}
		bookingRepository.saveAll(bookings);
	}
}
