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
 * Service class for
 * business logic of creating
 * guests, searching, deleting
 * and updating
 */
@Service
public class GuestService {

	private final GuestRepository guestRepository;

	/**
	 * Constructor that takes
	 * repository object which is annotated as
	 * Autowired and the repository itself as bean (@Repository)
	 */
	@Autowired
	public GuestService(GuestRepository guestRepository) {
		this.guestRepository = guestRepository;
	}

	/**
	 * Gets a list of all the guests -
	 * if there are any
	 *
	 * @return list of all the guests
	 */
	public List<Guest> findAll() {
		return guestRepository.findAll();
	}

	/**
	 * Tries to find a guest with given ID
	 * returns the Guest object if found
	 *
	 * @param id id of the guest
	 * @return Guest object if one is found
	 * @throws ItemNotFoundException if a guest with the specified ID is not found
	 **/
	public Guest findById(int id) {
		if (!guestRepository.existsById(id)) {
			throw new ItemNotFoundException("User does not exist");
		}
		return guestRepository.findById(id);
	}

	/**
	 * Creates new guest
	 *
	 * @param item the new guest
	 * @throws ArgumentNotValidException if the guest has invalid fields or is null
	 */
	public void save(Guest item) {
		validateGuest(item);
		guestRepository.save(item);
	}

	/**
	 * Creates list of new guests
	 *
	 * @param guests the list of new guests
	 */
	public void saveAll(List<Guest> guests) {
		validateGuestList(guests);
		guestRepository.saveAll(guests);
	}

	/**
	 * Saves multiple guests
	 * Takes varargs and checks each arg
	 * separately for validity
	 *
	 * @param items Guest varargs
	 */
	public void saveAll(Guest... items) {
		validateGuestList(Arrays.asList(items));
		guestRepository.saveAll(items);
	}

	/**
	 * Updates an existing guest
	 *
	 * @param guest the guest that will be updated
	 * @return the updated guest
	 */
	public Guest updateGuest(Guest guest) {
		validateGuest(guest);
		findById(guest.getGuestId());
		return guestRepository.updateGuest(guest);
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
	 * Deletes a guest
	 * if it finds one matching
	 *
	 * @param guest the guest that will be removed
	 * @return true if the guest if successfully removed
	 */
	public boolean deleteGuest(Guest guest) {
		validateGuest(guest);
		return guestRepository.delete(findById(guest.getGuestId()));
	}

	/**
	 * Deletes all guests
	 */
	public void deleteAll() {
		guestRepository.deleteAll();
	}

	private void validateGuestList(List<Guest> guests) {
		if (guests == null) {
			throw new FailedInitializationException("Invalid list of guests!");
		}
		for (Guest guest : guests) {
			validateGuest(guest);
		}
	}

	private void validateGuest(Guest guest) {
		if (guest == null) {
			throw new FailedInitializationException("Invalid guest!");
		}
		if (guest.getFirstName() == null || guest.getLastName() == null || guest.getGender() == null
			|| guest.getFirstName().isEmpty() || guest.getLastName().isEmpty()) {
			throw new FailedInitializationException("Invalid guest fields!");

		}
	}
}
