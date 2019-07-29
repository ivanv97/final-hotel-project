package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Booking;
import eu.deltasource.internship.hotel.exception.FailedInitializationException;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.BookingRepository;


import java.time.LocalDate;
import java.util.List;

/**
 * Created by Taner Ilyazov - Delta Source Bulgaria on 2019-07-28.
 */
public class BookingService {

    private final BookingRepository bookingRepository;

    private final RoomService roomService;

    private final GuestService guestService;

    public BookingService(BookingRepository bookingRepository, RoomService roomService, GuestService guestService) {
        this.bookingRepository = bookingRepository;
        this.roomService = roomService;
        this.guestService = guestService;
    }

    /**
     * Searches booking by ID
     *
     * @param ID booking's ID
     * @return the found booking
     */
    public Booking findByID(int ID) {
        return this.bookingRepository.findById(ID);
    }

    /**
     * Deletes booking by ID
     *
     * @param ID booking's ID
     */
    public boolean deleteByID(int ID) {
        if (!this.bookingRepository.deleteById(ID)) {
            throw new ItemNotFoundException("Booking with such ID does not exits!");
        }
        return true;
    }

    /**
     * Under construction
     */
    public void updateDates(int bookingID, LocalDate from, LocalDate to) {
        Booking booking = bookingRepository.findById(bookingID);

        for (Booking book : bookingRepository.findAll()) {
            if (checkOverlapping(from, to)==true){
                throw new FailedInitializationException("Dates overlapping!");
            }
        }

    }

    private boolean checkOverlapping(LocalDate from, LocalDate to) {
        if (from.isAfter(to) || from.equals(to))
            return true;
        return false;
    }

    public void save(Booking item) {
        this.bookingRepository.save(item);
    }

    public List<Booking> findAll() {
        return this.bookingRepository.findAll();
    }

    private boolean checkIfBooked(Booking item) {
        if (this.bookingRepository.findAll().isEmpty()) {
            return false;
        }
        for (Booking booking : this.bookingRepository.findAll()) {
            if (booking.getRoomId() == item.getRoomId()) {
                if (item.getFrom() != null && item.getTo() != null && item.getTo().isAfter(item.getFrom())) {
                    if (!item.getFrom().isBefore(booking.getTo()) || !item.getTo().isAfter(booking.getFrom())) {
                        return false;
                    }
                } else {
                    throw new FailedInitializationException("The dates passed are not correct!");
                }
            }
        }
        return true;
    }
}