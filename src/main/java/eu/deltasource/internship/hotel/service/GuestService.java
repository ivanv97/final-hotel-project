package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Guest;
import eu.deltasource.internship.hotel.exception.ArgumentNotValidException;
import eu.deltasource.internship.hotel.exception.FailedInitializationException;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.GuestRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * Represents services for guests
 */
@Service
public class GuestService {

	private final GuestRepository guestRepository;

	@Autowired
	public GuestService(GuestRepository guestRepository) {
		this.guestRepository = guestRepository;
	}


	public Guest findById(int id) {
		if (!guestRepository.existsById(id)) {
			throw new ItemNotFoundException("User does not exist");
		}
		return guestRepository.findById(id);
	}

	/**
	 * Deletes guest by id
	 *
	 * @param id guest's id
	 * @return true if the guest is successfully removed
	 */
	public boolean deleteById(int id) {
		if (guestRepository.existsById(id)) {
			return guestRepository.deleteById(id);
		}
		throw new ItemNotFoundException("Guest with id " + id + " does not exist!");
	}

	/**
	 * Deletes guest
	 *
	 * @param guest the guest that will be removed
	 * @return true if the guest if successfully removed
	 */
	public boolean deleteGuest(Guest guest) {
		if (!guestRepository.existsById(guest.getGuestId())) {
			throw new ItemNotFoundException("No guest with such ID!");
		}
		validGuest(guest);
		return guestRepository.delete(findById(guest.getGuestId()));
	}

	/**
	 * Deletes all guests
	 */
	public void deleteAll() {
		if (guestRepository.count() == 0) {
			throw new ItemNotFoundException("Empty list of guests can not be deleted!");
		}
		guestRepository.deleteAll();
	}

	public List<Guest> findAll() {
		return guestRepository.findAll();
	}

	/**
	 * Creates new guest
	 *
	 * @param item the new guest
	 * @throws ArgumentNotValidException if the guest has invalid fields
	 *                                   or is null
	 */
	public void save(Guest item) {
		validGuest(item);
		guestRepository.save(item);
	}


	/**
	 * Creates list of new guests
	 * <p>
	 * <<<<<<< HEAD
	 *
	 * @param guests the list of new guests
	 */
	public void saveAll(List<Guest> guests) {
		validGuests(guests);
		guestRepository.saveAll(guests);
	}

	/**
	 * Creates array of new guests which is converted to list
	 *
	 * @param guests array of guests
	 */
	public void saveAll(Guest... guests) {
		saveAll(Arrays.asList(guests));
	}

	/**
	 * Updates an existing guest
	 *
	 * @param guest the guest that will be updated
	 * @return the updated guest
	 */
	public Guest updateGuest(Guest guest) {
		if (!guestRepository.existsById(guest.getGuestId())) {
			throw new ItemNotFoundException("No guest with such ID!");
		}
		validGuest(guest);
		return guestRepository.updateGuest(guest);
	}

	private void validGuests(List<Guest> guests) {
		if (guests == null) {
			throw new FailedInitializationException("Invalid list of guests!");
		}
		if (guests.isEmpty()) {
			throw new FailedInitializationException("Empty list of guests!");
		}
		for (Guest guest : guests) {
			validGuest(guest);
		}
	}

	private void validGuest(Guest guest) {
		if (guest == null) {
			throw new FailedInitializationException("Invalid guest!");
		}
		if (guest.getFirstName() == null || guest.getLastName() == null || guest.getGender() == null
			|| guest.getFirstName().isEmpty() || guest.getLastName().isEmpty()) {
			throw new FailedInitializationException("Invalid guest fields!");
		}
	}
}

