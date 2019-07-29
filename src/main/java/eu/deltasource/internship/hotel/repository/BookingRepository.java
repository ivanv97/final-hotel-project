package eu.deltasource.internship.hotel.repository;

import eu.deltasource.internship.hotel.domain.Booking;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Taner Ilyazov - Delta Source Bulgaria on 2019-07-28.
 */
public class BookingRepository {

    private final List<Booking> repository;

    /**
     * Default constructor, which initializes the repository
     * as an empty ArrayList.
     */
    public BookingRepository() {
        repository = new ArrayList<>();
    }

    /**
     * Returns an unmodifiable list of all items
     * currently in the repository.
     */
    public List<Booking> findAll() {
        return Collections.unmodifiableList(repository);
    }

    /**
     * Method, which checks the repository if
     * there is an item available with the given id.
     *
     * Check this always, before using operations with id's.
     */
    public boolean existsById(int id) {
        for (Booking item : repository) {
            if (item.getBookingId() == id)
                return true;
        }
        return false;
    }

    /**
     * Returns an item from the repository
     */
    public Booking findById(int id) {
        for (Booking item : repository) {
            if (item.getBookingId() == id)
                return new Booking(item);
        }
        throw new ItemNotFoundException("A booking with id: " + id + " was not found!");
    }

    /**
     * Saves the item in the repository with a new id
     * using the item count in the repository
     */
    public void save(Booking item) {
        Booking newBooking = new Booking(count() + 1, item.getGuestId(), item.getRoomId(),
                item.getNumberOfPeople(), item.getFrom(), item.getTo());
        repository.add(newBooking);
    }

    /**
     * Saves the list of items in the repository
     */
    public void saveAll(List<Booking> items) {
        items.forEach(
                this::save);
    }

    /**
     * Saves all given items in the repository
     */
    public void saveAll(Booking... items) {
        saveAll(Arrays.asList(items));
    }

    /**
     * Updates the dates of a given booking
     * <p>
     * You can only update the Date of a booking,
     * if you need to update something else a new
     * Booking has to be created and this one needs to be removed.
     * <p>
     * All validations should be done in the service layer!!!
     */
    public Booking updateDates(Booking item) {
        Booking updatedBooking = findById(item.getBookingId());
        updatedBooking.setBookingDates(item.getFrom(), item.getTo());
        return updatedBooking;
    }

    /**
     * Removes an item from the repository
     * by searching for an exact match.
     * <p>
     * Returns true if an exact match is and deleted,
     * returns false if there's no match and the list is unchanged.
     */
    public boolean delete(Booking item) {
        return repository.remove(item);
    }

    /**
     * Removes the item in the repository
     * matching the given id.
     * <p>
     * Returns true if there's a match and is deleted,
     * returns false if there's no match and the list is unchanged.
     */
    public boolean deleteById(int id) {
        Booking item = findById(id);
        return delete(item);
    }

    /**
     * Deletes all items in the repository
     */
    public void deleteAll() {
        repository.clear();
    }

    /**
     * Returns the number of items left in the repository
     */
    public int count() {
        return repository.size();
    }
}
