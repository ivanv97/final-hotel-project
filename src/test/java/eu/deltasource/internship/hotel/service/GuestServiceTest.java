package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Gender;
import eu.deltasource.internship.hotel.domain.Guest;
import eu.deltasource.internship.hotel.exception.FailedInitializationException;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.GuestRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GuestServiceTest {

    private GuestRepository guestRepository;
    private GuestService guestService;
    private Guest firstGuest;
    private Guest secondGuest;

    @BeforeEach
    public void setUp() {
        guestRepository = new GuestRepository();
        guestService = new GuestService(guestRepository);
        firstGuest = new Guest(1, "John", "Miller", Gender.MALE);
        secondGuest = new Guest(2, "Marta", "Peterson", Gender.FEMALE);

        guestRepository.saveAll(firstGuest, secondGuest);
    }

    @Test
    public void findGuestByExistingId() {
        //given
        // two guests already exist
        int guestId = 1;

        //when
        Guest expectedGuest = guestService.findById(guestId);

        //then
        assertTrue(expectedGuest.equals(firstGuest));
    }

    @Test
    public void findGuestByIdThatDoesNotExist() {
        //given
        // two guests already exists
        int guestId = -4;

        //when and then
        assertThrows(ItemNotFoundException.class, () -> guestService.findById(guestId));
    }

    @Test
    public void deleteGuestByExistingId() {
        //given
        // two guests already exist
        int id = 1;
        boolean expectedResult = true;

        //when
        boolean result = guestService.deleteById(id);

        //then
        assertEquals(expectedResult, result);
        assertThrows(ItemNotFoundException.class, () -> guestService.findById(id));
    }

    @Test
    public void deleteGuestByIdThatDoesNotExist() {
        //given
        // two guests already exist
        int id = 123;

        //when and then
        assertThrows(ItemNotFoundException.class, () -> guestService.deleteById(id));
    }

    @Test
    public void deleteExistingGuest() {
        //given
        // two guests already exist
        int removedGuestId = 2, expectedSize = 1;
        boolean expectedResult = true;

        //when
        boolean result = guestService.deleteGuest(secondGuest);

        //then
        assertEquals(expectedResult, result);
        assertEquals(expectedSize, guestService.findAll().size());
        assertThrows(ItemNotFoundException.class, () -> guestService.findById(removedGuestId));
    }

    @Test
    public void deleteGuestThatDoesNotExist() {
        //given
        // two guests already exist
        int guestId = 3;
        Guest newGuest = new Guest(guestId, "Maya", "House", Gender.FEMALE);

        //when and then
        assertThrows(FailedInitializationException.class, () -> guestService.deleteGuest(null));
        assertThrows(ItemNotFoundException.class, () -> guestService.deleteGuest(newGuest));
    }

    @Test
    public void deleteAllExistingGuests() {
        //given
        // two guests already exist

        //when
        guestService.deleteAll();

        //then
        assertThrows(ItemNotFoundException.class, () -> guestService.findAll());
    }

    @Test
    public void deleteAllGuestsThatDoNotExist() {
        //given
        GuestRepository guestRepository = new GuestRepository();
        GuestService guestService = new GuestService(guestRepository);

        //when and then
        assertThrows(ItemNotFoundException.class, () -> guestService.deleteAll());
    }

    @Test
    public void createGuestSuccessfully() {
        //given
        // two guests already exist
        int guestId = 3;
        int expectedListOfGuestSize = 3;
        Guest guest = new Guest(guestId, "Daniel", "Garcia", Gender.MALE);

        //when
        guestService.save(guest);

        //then
        assertEquals(expectedListOfGuestSize, guestService.findAll().size());
        assertTrue(guest.equals(guestService.findById(guestId)));
    }

    @Test
    public void createGuestUnsuccessfully() {
        //given

        //when and then
        assertThrows(FailedInitializationException.class, () -> guestService.save(null));
        assertThrows(FailedInitializationException.class,
                () -> guestService.save(new Guest(1, "", "Jackson", null)));
        assertThrows(FailedInitializationException.class,
                () -> guestService.save(new Guest(1, "Martin", "", Gender.MALE)));
    }

    @Test
    public void createListOfGuestsSuccessfully() {
        //given
        List<Guest> guests = new ArrayList<>();
        guests.add(new Guest(1, "Mariya", "Miller", Gender.FEMALE));
        guests.add(new Guest(2, "Sean", "Jean", Gender.MALE));
        int listSize = guests.size();
        GuestRepository guestRepository = new GuestRepository();
        GuestService guestService = new GuestService(guestRepository);

        //when
        guestService.saveAll(guests);

        //then
        assertEquals(listSize, guestService.findAll().size());
        assertTrue(guestService.findAll().contains(guests.get(0)));
        assertTrue(guestService.findAll().contains(guests.get(1)));
    }

    @Test
    public void createListOfGuestsUnsuccessfully() {
        //given
        List<Guest> guests = new ArrayList<>();
        List<Guest> guestList = new ArrayList<>();
        guestList.add(null);

        //when and then
        assertThrows(FailedInitializationException.class, () -> guestService.saveAll(guests));
        assertThrows(FailedInitializationException.class, () -> guestService.saveAll(guestList));
    }

    @Test
    public void findAllExistingGuests() {
        //given
        // two guests already exist
        int expectedSize = 2;

        //when
        List<Guest> expectedGuests = guestService.findAll();

        //then
        assertEquals(expectedSize, expectedGuests.size());
    }

    @Test
    public void findAllGuestsThatDoNotExist() {
        //given
        GuestRepository guestRepository = new GuestRepository();
        GuestService guestService = new GuestService(guestRepository);

        //when and then
        assertThrows(ItemNotFoundException.class, () -> guestService.findAll());
    }

    @Test
    public void updateGuestSuccessfully() {
        //given
        // two guests already exist
        int guestId = 1;
        Guest updatedGuest = new Guest(guestId, "Tea", "Toh", Gender.FEMALE);

        //when
        Guest expectedGuest = guestService.updateGuest(updatedGuest);

        //then
        assertTrue(expectedGuest.getFirstName().equals(updatedGuest.getFirstName()));
        assertTrue(expectedGuest.getLastName().equals(updatedGuest.getLastName()));
        assertTrue(expectedGuest.getGender().equals(updatedGuest.getGender()));
    }

    @Test
    public void updateGuestUnsuccessfully() {
        //given
        // two guests already exist
        int invalidGuestId = 7, validGuestId = 1;
        Guest updatedGuest = new Guest(invalidGuestId, "Tea", "Toh", Gender.FEMALE);

        //when and then
        // guest with such id does not exist
        assertThrows(ItemNotFoundException.class, () -> guestService.updateGuest(updatedGuest));
        // guest without first name
        assertThrows(FailedInitializationException.class,
                () -> guestService.updateGuest(new Guest(validGuestId, "", "Johnson", Gender.MALE)));
        // guest without last name
        assertThrows(FailedInitializationException.class,
                () -> guestService.updateGuest(new Guest(validGuestId, "Joe", "", Gender.MALE)));
        // guest without gender parameter
        assertThrows(FailedInitializationException.class,
                () -> guestService.updateGuest(new Guest(validGuestId, "Joe", "Johnson", null)));
    }

    @AfterEach
    public void tearDown() {
        guestService = null;
        guestRepository = null;
        firstGuest = null;
        secondGuest = null;
    }
}