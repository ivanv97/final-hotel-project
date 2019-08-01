package eu.deltasource.internship.hotel.service;


import eu.deltasource.internship.hotel.domain.Gender;
import eu.deltasource.internship.hotel.domain.Guest;
import eu.deltasource.internship.hotel.exception.FailedInitializationException;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.GuestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class GuestServiceTest {
	private GuestRepository repo;
	private GuestService service;
	private Guest guest1;
	private Guest guest2;
	private Guest guest3;

	@BeforeEach
	public void setUp() {
		repo = new GuestRepository();
		service = new GuestService(repo);
		guest1 = new Guest(1, "Ivan", "Velkushanov", Gender.MALE);
		guest2 = new Guest(2, "Ivaylo", "Hahaha", Gender.MALE);
		guest3 = new Guest(3, "Adelina", "Hahaha", Gender.FEMALE);
		service.save(guest1);
		service.save(guest2);
		service.save(guest3);
	}

	@Test
	public void findByIdShouldWorkWhenSearchingWithValidId() {
		assertEquals(guest1, service.findById(guest1.getGuestId()));
	}

	@Test
	public void findByIdShouldThrowExcIfNoGuestWithSpecifiedId() {
		assertThrows(ItemNotFoundException.class, () -> service.findById(guest3.getGuestId() + 1));
	}

	@Test
	public void updateGuestShouldWorkProperlyIfUpdatingExistingGuest() {
		//Given
		Guest updatedGuest = new Guest(guest1.getGuestId(), "Ivan", "Petrov", Gender.MALE);

		//When

		//Then
		assertEquals(updatedGuest, service.updateGuest(updatedGuest));
	}

	@Test
	public void updateGuestShouldThrowExcWhenTryingToUpdateNonExistingGuest() {
		//Given
		Guest updatedGuest = new Guest(guest3.getGuestId() + 1, "Ivan", "Petrov", Gender.MALE);

		//When

		//Then
		assertThrows(ItemNotFoundException.class, () -> service.updateGuest(updatedGuest));
	}

	@Test
	public void deleteByIdShouldReturnTrueIfGuestWasDeleted() {
		assertTrue(service.deleteById(guest1.getGuestId()));
	}

	@Test
	public void deleteByIdShouldReturnFalseIfGuestWithThisIdNotFound() {
		assertThrows(ItemNotFoundException.class, () -> service.deleteById(guest3.getGuestId() + 1));
	}

	@Test
	public void findAllShouldReturnAllGuestsIfAny() {
		assertTrue(service.findAll().containsAll(Arrays.asList(guest1, guest2, guest3))
			&& service.findAll().size() == 3);
	}

	@Test
	public void findAllShouldReturnEmptyListIfNoGuests() {
		//When
		service.deleteById(guest1.getGuestId());
		service.deleteById(guest2.getGuestId());
		service.deleteById(guest3.getGuestId());

		//Then
		assertTrue(service.findAll().isEmpty());
	}

	@Test
	public void saveShouldThrowExcIfPassedArgumentIsNull() {
		assertThrows(FailedInitializationException.class, () -> service.save(null));
	}

	@Test
	public void saveShouldWorkIfProperObjectPassed() {
		//Given
		Guest newGuest = new Guest(1, "Hristo", "Gluhov", Gender.MALE);

		//When

		//Then
		assertDoesNotThrow(() -> service.save(newGuest));
	}

	@Test
	public void saveAllShouldThrowExcIfNoArguments() {
		assertThrows(FailedInitializationException.class, () -> service.saveAll());
	}

	@Test
	public void saveAllShouldWorkWhenPassedVarargs() {
		assertDoesNotThrow(() -> service.saveAll(guest1, guest2, guest3));
	}

	@Test
	public void saveAllShouldThrowExcWhenEmptyListPassed() {
		assertThrows(FailedInitializationException.class, () -> service.saveAll(new ArrayList<Guest>()));
	}

	@Test
	public void saveAllShouldWorkWhenGivenProperList() {
		assertDoesNotThrow(() -> service.saveAll(new ArrayList<>(Arrays.asList(guest1, guest2, guest3))));
	}
}

