package com.unison.appartment.model;

import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.Objects;

/**
 * Classe che rappresenta un premio da reclamare
 */
public class Reward implements Serializable {

    private final static String ATTRIBUTE_RESERVATION_NAME = "reservation-name";
    private final static String ATTRIBUTE_RESERVATION_ID = "reservation-id";

    @Exclude
    private String id;
    private String name;
    @Nullable
    private String description;
    private int points;
    @Nullable
    @PropertyName(ATTRIBUTE_RESERVATION_ID)
    private String reservationId;
    @Nullable
    @PropertyName(ATTRIBUTE_RESERVATION_NAME)
    private String reservationName;

    public Reward() {}

    public Reward(String name, String description, int points) {
        this.name = name;
        this.description = description;
        this.points = points;
    }

    public Reward(String name, String description, int points, String reservationId) {
        this(name, description, points);
        this.reservationId = reservationId;
    }

    @Nullable
    @PropertyName(ATTRIBUTE_RESERVATION_ID)
    public String getReservationId() {
        return reservationId;
    }

    @PropertyName(ATTRIBUTE_RESERVATION_ID)
    public void setReservationId(@Nullable String reservationId) {
        this.reservationId = reservationId;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Nullable
    @PropertyName(ATTRIBUTE_RESERVATION_NAME)
    public String getReservationName() {
        return reservationName;
    }

    @PropertyName(ATTRIBUTE_RESERVATION_NAME)
    public void setReservationName(@Nullable String reservationName) {
        this.reservationName = reservationName;
    }

    @Exclude
    public boolean isRequested() {
        return this.reservationId != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reward reward = (Reward) o;
        return points == reward.points &&
                name.equals(reward.name) &&
                Objects.equals(description, reward.description) &&
                Objects.equals(reservationId, reward.reservationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, points, reservationId);
    }
}
