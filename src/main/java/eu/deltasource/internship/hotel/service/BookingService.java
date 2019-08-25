package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Booking;
import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.exception.ArgumentNotValidException;
import eu.deltasource.internship.hotel.exception.BookingOverlappingException;
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
	 * Validates and creates new booking
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
	 * Searches for and books the
	 * first available room that has the
	 * appropriate capacity and there are
	 * no conflicts in the dates of the
	 * bookings
	 *
	 * @param newBooking all that matters here is guestId, number of people,
	 *                   from and to date, roomId is irrelevant
	 * @throws BookingOverlappingException if there are no free rooms for the desired interval
	 */
	public void findAndBookFirstAvailableRoom(Booking newBooking) {
		validateDates(newBooking.getFrom(), newBooking.getTo());
		guestService.findById(newBooking.getGuestId());

		for (Room room : roomService.findRooms()) {
			boolean isVacant = true;
			for (Booking booking : findAll()) {
				boolean isBookingComparable = room.getRoomId() == booking.getRoomId();
				boolean isCapacityNotEnough = room.getRoomCapacity() < newBooking.getNumberOfPeople();
				boolean doesOverlap = newBooking.getTo().isAfter(booking.getFrom()) && newBooking.getFrom().isBefore(booking.getTo());
				if (isBookingComparable && (isCapacityNotEnough || doesOverlap)) {
					isVacant = false;
					break;
				}
			}
			if (isVacant) {
				bookingRepository.save(new Booking(1, newBooking.getGuestId(), room.getRoomId(),
					newBooking.getNumberOfPeople(), newBooking.getFrom(), newBooking.getTo()));
				return;
			}
		}
		throw new BookingOverlappingException("Cannot create booking for the specified interval");
	}

	/**
	 * Updates booking by either room id or number of people
	 *
	 * @param updatedBooking the new booking
	 * @throws ItemNotFoundException     if the booking we wish to update
	 *                                   doesn't match any existing ones
	 * @throws ArgumentNotValidException if the updated booking differs in guestId
	 *                                   than its previous version
	 */
	public void updateBooking(Booking updatedBooking) {
		if (!bookingRepository.existsById(updatedBooking.getBookingId())) {
			throw new ItemNotFoundException("Booking with id " + updatedBooking.getBookingId() + " does not exist!");
		}
		if (bookingRepository.findById(updatedBooking.getBookingId()).getGuestId() != updatedBooking.getGuestId()) {
			throw new ArgumentNotValidException("Booking to be updated must have the same guest ID!");
		}
		validateBooking(updatedBooking);
		validateUpdateBooking(updatedBooking);
		deleteById(updatedBooking.getBookingId());
		save(updatedBooking);
	}

	/**
	 * Updates booking by dates
	 *
	 * @param bookingId id of the booking that
	 *                  is to be updated
	 * @param from      starting date
	 * @param to        ending date
	 * @return updated booking
	 * @throws BookingOverlappingException if the desired dates are not free
	 */
	public Booking updateBookingByDates(int bookingId, LocalDate from, LocalDate to) {
		validateDates(from, to);
		Booking booking = findById(bookingId);
		Booking updatedBooking = new Booking(bookingId, booking.getGuestId(), booking.getRoomId(), booking.getNumberOfPeople(),
			from, to);
		if (!validateBookingUpdateDates(from, to, booking.getRoomId(), bookingId)) {
			throw new BookingOverlappingException("Overlapping dates!");
		}
		return bookingRepository.updateDates(updatedBooking);
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
	 * @throws ItemNotFoundException if the id passed is non existing
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

	/**
	 * Validates booking that is to be
	 * updated - first checks if the guest
	 * if is the same - else throws exception
	 * Then it validates the new dates
	 *
	 * @param booking
	 * @throws ArgumentNotValidException if the guest id is different than the
	 *                                       current one
	 * @throws BookingOverlappingException   if the
	 */
	private void validateUpdateBooking(Booking booking) {
		if (booking.getGuestId() != findById(booking.getBookingId()).getGuestId()) {
			throw new ArgumentNotValidException("You are not allowed to change guest id!");
		}
		if (!validateBookingUpdateDates(booking.getFrom(), booking.getTo(), booking.getRoomId(), booking.getBookingId())) {
			throw new BookingOverlappingException("The room is already booked for this period!");
		}
	}

	/**
	 * Validates booking dates for a particular room
	 *
	 * @param from       the new from date
	 * @param to         the new to date
	 * @param roomId     the room we are checking bookings for
	 * @param bookingId  the booking we are about to update dates for
	 * @param idToIgnore optional parameter - is ignored if we enter
	 *                   recursion - this happens so that it doesn't think
	 *                   it has conflicts in dates when comparing the booking to
	 *                   itself
	 * @return true if no conflicts and dates are OK to be updated,
	 * false if there is conflict somewhere
	 */
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

	/**
	 * Validates a booking object
	 * Should not be null and should
	 * have valid fields
	 *
	 * @param booking the object to be validated
	 * @throws ArgumentNotValidException if anything is wrong with the booking
	 *                                       object
	 */
	private void validateBooking(Booking booking) {
		if (booking == null || !validateBookingFields(booking)) {
			throw new ArgumentNotValidException("Invalid Booking!");
		}
	}

	/**
	 * First validates dates and checks if
	 * there is such guest in guest repo
	 * Then it verifies if the desired number of
	 * people matches room capacity
	 *
	 * @param booking the booking to be validated
	 * @return true if each field of the booking is valid
	 */
	private boolean validateBookingFields(Booking booking) {
		validateDates(booking.getFrom(), booking.getTo());
		guestService.findById(booking.getGuestId());

		return roomService.getRoomById(booking.getRoomId()).getRoomCapacity() >= booking.getNumberOfPeople();
	}

	/**
	 * To date has to be at least one
	 * day after from date and both dates
	 * should not be null
	 */
	private void validateDates(LocalDate from, LocalDate to) {
		if (from == null || to == null || from.isAfter(to) || from.equals(to)) {
			throw new ArgumentNotValidException("Invalid dates!");
		}
	}

	/**
	 * Validates the dates of a new booking
	 * No need of checking if it already exists and
	 * avoiding unnecessary checks - we just perform
	 * check on every booking for the particular room
	 *
	 * @param from   booking starts from
	 * @param to     continues until
	 * @param roomId the room whose bookings
	 *               we are going to check for overlapping
	 */
	private void validateBookingCreationDates(LocalDate from, LocalDate to, int roomId) {
		for (Booking booking : findAll()) {
			boolean isComparable = booking.getRoomId() == roomId;
			if (isComparable && to.isAfter(booking.getFrom()) && from.isBefore(booking.getTo())) {
				throw new BookingOverlappingException("The booking can not be created because dates are overlapped!");
			}
		}
	}
}
