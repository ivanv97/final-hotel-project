package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Guest;
import eu.deltasource.internship.hotel.exception.ArgumentNotValidException;
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
	 * autowired and the repository itself as bean (@Repository)
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
			throw new ItemNotFoundException("Guest does not exist");
		}
		return guestRepository.findById(id);
	}

	/**
	 * Creates new guest
	 *
	 * @param item the new guest
	 */
	public void save(Guest item) {
		validateGuest(item);
		guestRepository.save(item);
	}

	/**
	 * Saves all of the guests passed
	 * as list to the repository by
	 * calling the overloaded varargs
	 * version of the method
	 *
	 * @param guests the list of new guests
	 */
	public void saveAll(List<Guest> guests) {
		saveAll(guests.toArray(new Guest[guests.size()]));
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
	 * Validates the updated
	 * version beforehand
	 *
	 * @param guest the guest that will be updated
	 * @return the updated guest
	 * @throws ItemNotFoundException if we try to
	 *                               update non-existing guest
	 */
	public Guest updateGuest(Guest guest) {
		validateGuest(guest);
		if (!guestRepository.existsById(guest.getGuestId())) {
			throw new ItemNotFoundException("Guest cannot be updated - does not exist.");
		}
		return guestRepository.updateGuest(guest);
	}

	/**
	 * Deletes guest by id
	 *
	 * @param id guest's id
	 * @return true if the guest is successfully removed
	 * @throws ItemNotFoundException if the id passed
	 *                               does not match any existing id in repo
	 */
	public boolean deleteById(int id) {
		if (!guestRepository.existsById(id)) {
			throw new ItemNotFoundException("Guest with id " + id + " does not exist!");
		}
		return guestRepository.deleteById(id);
	}

	/**
	 * Deletes a guest
	 * if it finds one matching
	 *
	 * @param guest the guest that will be removed
	 * @return true if the guest if successfully removed
	 * @throws ItemNotFoundException if we do not
	 *                               find a matching id in repo
	 */
	public boolean deleteGuest(Guest guest) {
		validateGuest(guest);
		if (!guestRepository.existsById(guest.getGuestId())) {
			throw new ItemNotFoundException("Guest with id " + guest.getGuestId() + " does not exist!");
		}
		return guestRepository.delete(findById(guest.getGuestId()));
	}

	/**
	 * Deletes all guests
	 */
	public void deleteAll() {
		guestRepository.deleteAll();
	}

	/**
	 * Validates a list guest by guest
	 * Ensures list is not null
	 *
	 * @param guests the guest list we are to verify
	 * @throws ArgumentNotValidException if the list itself is null
	 */
	private void validateGuestList(List<Guest> guests) {
		if (guests == null) {
			throw new ArgumentNotValidException("List of guests cannot be null!");
		}
		for (Guest guest : guests) {
			validateGuest(guest);
		}
	}

	/**
	 * Validates the guest passed as parameter -
	 * should have assigned gender and names should
	 * not be null or empty strings
	 *
	 * @param guest the guest to be validated
	 * @throws ArgumentNotValidException if the guest is null or if
	 *                                       any of the fields does not comply with requirements
	 */
	private void validateGuest(Guest guest) {
		if (guest == null) {
			throw new ArgumentNotValidException("Guest cannot be null!");
		}
		if (guest.getFirstName() == null || guest.getLastName() == null || guest.getGender() == null
			|| guest.getFirstName().isEmpty() || guest.getLastName().isEmpty()) {
			throw new ArgumentNotValidException("Invalid guest fields!");
		}
	}
}
