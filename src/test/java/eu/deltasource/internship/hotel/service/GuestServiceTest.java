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
    GuestRepository guestRepository = new GuestRepository();
    GuestService guestService = new GuestService(guestRepository);
    Guest firstGuest;
    Guest secondGuest;

    @BeforeEach
    public void setUp() {
        firstGuest = new Guest(1, "John", "Miller", Gender.MALE);
        secondGuest = new Guest(2, "Marta", "Peterson", Gender.FEMALE);

        guestRepository.saveAll(firstGuest, secondGuest);
    }

    @Test
    public void findGuestByExistingId() {
        //given
        // two guests already exist
        int guestID = 1;

        //when
        Guest expectedGuest = guestService.findById(guestID);

        //then
        assertTrue(expectedGuest.equals(firstGuest));
    }

    @Test
    public void findGuestByIdThatDoesNotExist() {
        //given
        // two guests already exists
        int guestID = -4;

        //when and then
        assertThrows(ItemNotFoundException.class, () -> guestService.findById(guestID));
    }

    @Test
    public void deleteByExistingId() {
        //given
        // two guests already exist
        int id = 1;

        //when
        boolean result = guestService.deleteById(id);

        //then
        assertEquals(true, result);
        assertThrows(ItemNotFoundException.class, () -> guestService.findById(id));
    }

    @Test
    public void deleteByIdThatDoesNotExist() {
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
        int guestId = 2;
        int expectedSize = 1;

        //when
        boolean result = guestService.deleteGuest(secondGuest);

        //then
        assertEquals(true, result);
        assertEquals(expectedSize, guestService.findAll().size());
        assertThrows(ItemNotFoundException.class, () -> guestService.findById(guestId));
    }

    @Test
    public void deleteGuestThatDoesNotExist() {
        //given
        // two guests already exist
        int guestId = 3;
        Guest newGuest = new Guest(guestId, "Maya", "House", Gender.FEMALE);

        //when and then
        assertThrows(FailedInitializationException.class,
                () -> guestService.deleteGuest(null));
        assertThrows(ItemNotFoundException.class,
                () -> guestService.deleteGuest(newGuest));
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
        assertThrows(FailedInitializationException.class,
                () -> guestService.save(null));
        assertThrows(FailedInitializationException.class,
                () -> guestService.save(
                        new Guest(1, "", "Jackson", null)));
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
    public void updateGuest() {
        //given
        // two guests already exist
        int guestID = 1;
        Guest updatedGuest = new Guest(guestID, "Tea", "Toh", Gender.FEMALE);

        //when
        Guest expectedGuest = guestService.updateGuest(updatedGuest);

        //then
        assertTrue(expectedGuest.getFirstName().equals(updatedGuest.getFirstName()));
        assertTrue(expectedGuest.getLastName().equals(updatedGuest.getLastName()));
        assertTrue(expectedGuest.getGender().equals(updatedGuest.getGender()));
    }

    @AfterEach
    void tearDown() {
        firstGuest = null;
        secondGuest = null;
    }
}