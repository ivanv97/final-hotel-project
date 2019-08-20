package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Booking;
import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.exception.BookingOverlappingException;
import eu.deltasource.internship.hotel.exception.FailedInitializationException;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
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

	/**
	 * All arguments constructor
	 */
	@Autowired
	public BookingService(BookingRepository bookingRepository, RoomService roomService, GuestService guestService) {
		this.bookingRepository = bookingRepository;
		this.roomService = roomService;
		this.guestService = guestService;
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
	 * @param id dto be searched by
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
	 * Creates new booking
	 *
	 * @param newBooking the new booking
	 */
	public void save(Booking newBooking) {
		validateBooking(newBooking);
		validateBookingCreationDates(newBooking.getFrom(), newBooking.getTo(), newBooking.getRoomId());
		bookingRepository.save(newBooking);
	}

	/**
	 * Saves a list of booking objects
	 * Checks each one separately beforehand
	 *
	 * @param bookings The list we want dto save
	 */
	public void saveAll(List<Booking> bookings) {
		saveAll(bookings.toArray(new Booking[bookings.size()]));
	}

	/**
	 * Creates array of bookings
	 *
	 * @param bookings array of bookings
	 */
	public void saveAll(Booking... bookings) {
		for (Booking booking : bookings) {
			save(booking);
		}
	}

	/**
	 * Updates booking by either room id or number of people
	 *
	 * @param updatedBooking the new booking
	 * @throws ItemNotFoundException if the booking we wish to update
	 *                               doesn't match any existing ones
	 */
	public void updateBooking(Booking updatedBooking) {
		if (!bookingRepository.existsById(updatedBooking.getBookingId())) {
			throw new ItemNotFoundException("Booking with id " + updatedBooking.getBookingId() + " does not exist!");

		}
		validateBooking(updatedBooking);
		validateUpdateBooking(updatedBooking);
		deleteById(updatedBooking.getBookingId());
		save(updatedBooking);
	}

	/**
	 * Updates booking by dates
	 *
	 * @param bookingId booking's id that will be updated
	 * @param from      starting date
	 * @param to        ending date
	 * @return updated booking
	 * @throws BookingOverlappingException if the desired dates are not free
	 */
	public Booking updateBookingByDates(int bookingId, LocalDate from, LocalDate to) {
		validateDates(from, to);
		Booking booking = findById(bookingId);

		if (validateBookingUpdateDates(from, to, booking.getRoomId(), bookingId)) {
			booking.setBookingDates(from, to);
			return bookingRepository.updateDates(booking);
		}
		throw new BookingOverlappingException("Overlapping dates!");
	}

	/**
	 * Deletes booking
	 *
	 * @param booking the booking that will be deleted
	 * @return true if the booking is successfully deleted
	 */
	public boolean delete(Booking booking) {
		validateBooking(booking);
		return bookingRepository.delete(findById(booking.getBookingId()));
	}

	/**
	 * Deletes booking by id
	 *
	 * @param id booking's id
	 */
	public boolean deleteById(int id) {
		if (!bookingRepository.existsById(id)) {
			throw new ItemNotFoundException("Booking with id " + id + " does not exist!");
		}
		return bookingRepository.deleteById(id);
	}

	/**
	 * Deletes every single booking
	 * from the repo
	 */
	public void deleteAll() {
		bookingRepository.deleteAll();
	}

	private void validateUpdateBooking(Booking booking) {
		if (booking.getGuestId() != findById(booking.getBookingId()).getGuestId()) {
			throw new FailedInitializationException("You are not allowed to change guest id!");
		}
		if (!validateBookingUpdateDates(booking.getFrom(), booking.getTo(), booking.getRoomId(), booking.getBookingId())) {
			throw new BookingOverlappingException("The room is already booked for this period!");
		}
	}

	private boolean validateBookingUpdateDates(LocalDate from, LocalDate to, int roomId, int bookingId, int... idToIgnore) {
		for (Booking booking : findAll()) {
			if (booking.getRoomId() == roomId) {
				if (idToIgnore.length == 1 && booking.getBookingId() == idToIgnore[0]) {
					continue;
				}
				if (bookingId == booking.getBookingId()) {
					return (validateBookingUpdateDates(from, to, roomId, bookingId, bookingId));
				}
				if (to.isAfter(booking.getFrom()) && from.isBefore(booking.getTo())) {
					return false;
				}
			}
		}
		return true;
	}


	private void validateBooking(Booking booking) {
		if (booking == null || !validateBookingFields(booking)) {
			throw new FailedInitializationException("Invalid Booking!");
		}
	}

	private boolean validateBookingFields(Booking booking) {
		validateDates(booking.getFrom(), booking.getTo());

		boolean hasEnoughCapacity = roomService.getRoomById(booking.getRoomId()).getRoomCapacity() >=  booking.getNumberOfPeople();
		boolean isGuestSame = guestService.findById(booking.getGuestId()).getGuestId() == booking.getGuestId();

		return hasEnoughCapacity && isGuestSame;
	}

	private void validateDates(LocalDate from, LocalDate to) {
		if (from == null || to == null || from.isAfter(to) || from.equals(to)) {
			throw new FailedInitializationException("Invalid dates!");
		}
	}

	private void validateBookingCreationDates(LocalDate from, LocalDate to, int roomId) {
		for (Booking booking : findAll()) {
			boolean isComparable = booking.getRoomId() == roomId;
			if (isComparable && to.isAfter(booking.getFrom()) && from.isBefore(booking.getTo())) {
				throw new BookingOverlappingException("The booking can not be created because dates are overlapped!");
			}
		}
	}

	private void findAndBookFirstAvailableRoom(LocalDate from, LocalDate to, int guestId, int numberOfPeople){
		for(Room room : roomService.findRooms()){
			boolean isVacant = true;
			for(Booking booking : findAll()){
				if(room.getRoomId() == booking.getRoomId()){
					if(to.isAfter(booking.getFrom()) && from.isBefore(booking.getTo())){
						isVacant = false;
						break;
					}
				}
			}
			if(isVacant){
				save(new Booking(1, guestId, room.getRoomId(), numberOfPeople, from, to));
				return;
			}
		}
		throw new BookingOverlappingException("Cannot create booking in the specified interval");
	}
}
