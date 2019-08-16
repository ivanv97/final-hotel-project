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
			throw new ItemNotFoundException("Guest does not exist");
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
	 *
	 * @param guest the guest that will be updated
	 * @return the updated guest
	 */
	public Guest updateGuest(Guest guest) {
		validateGuest(guest);
		if(!guestRepository.existsById(guest.getGuestId())){
			throw new ItemNotFoundException("Guest cannot be updated - does not exist.");
		}
		return guestRepository.updateGuest(guest);
	}

	/**
	 * Deletes guest by id
	 *
	 * @param id guest's id
	 * @return true if the guest is successfully removed
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

	private void validateGuestList(List<Guest> guests) {
		if (guests == null) {
			throw new FailedInitializationException("List of guests cannot be null!");
		}
		for (Guest guest : guests) {
			validateGuest(guest);
		}
	}

	private void validateGuest(Guest guest) {
		if (guest == null) {
			throw new FailedInitializationException("Guest cannot be null!");
		}
		if (guest.getFirstName() == null || guest.getLastName() == null || guest.getGender() == null
			|| guest.getFirstName().isEmpty() || guest.getLastName().isEmpty()) {
			throw new FailedInitializationException("Invalid guest fields!");
		}
	}
}
