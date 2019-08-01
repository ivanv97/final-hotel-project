package eu.deltasource.internship.hotel.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import eu.deltasource.internship.hotel.exception.FailedInitializationException;

import java.time.LocalDate;

public class Date {

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate from;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
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