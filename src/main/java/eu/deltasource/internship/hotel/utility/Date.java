package eu.deltasource.internship.hotel.utility;


import eu.deltasource.internship.hotel.exception.FailedInitializationException;

import java.time.LocalDate;

public class Date {


    private LocalDate from;
    private LocalDate to;

    public Date(LocalDate from, LocalDate to) {
        setFrom(from);
        setTo(to);
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setFrom(LocalDate from) {
        if (from == null) {
            throw new FailedInitializationException("Invalid date !");
        }
        this.from = from;
    }

    public void setTo(LocalDate to) {
        if (to == null) {
            throw new FailedInitializationException("Invalid date !");
        }
        this.to = to;
    }
}
