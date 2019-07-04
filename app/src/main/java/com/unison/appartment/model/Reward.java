package com.unison.appartment.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.util.Objects;

/**
 * Classe che rappresenta un premio da reclamare
 */
public class Reward implements Parcelable, Comparable {

    private final static String ATTRIBUTE_RESERVATION_NAME = "reservation-name";
    private final static String ATTRIBUTE_RESERVATION_ID = "reservation-id";

    @Exclude
    private String id;
    private String name;
    private String description;
    private int points;
    @Nullable
    @PropertyName(ATTRIBUTE_RESERVATION_ID)
    private String reservationId;
    @Nullable
    @PropertyName(ATTRIBUTE_RESERVATION_NAME)
    private String reservationName;
    private int version;
    private boolean deleted;

    public Reward() {}

    public Reward(String id, String name, String description, int points) {
        this(name, description, points);
        this.id = id;
        this.deleted = false;
    }

    public Reward(String id, String name, String description, int points, int version) {
        this(name, description, points);
        this.id = id;
        this.version = version;
        this.deleted = false;
    }

    public Reward(String name, String description, int points) {
        this.name = name;
        this.description = description;
        this.points = points;
        this.version = 0;
        this.deleted = false;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reward reward = (Reward) o;
        return points == reward.points &&
                version == reward.version &&
                deleted == reward.deleted &&
                name.equals(reward.name) &&
                description.equals(reward.description) &&
                Objects.equals(reservationId, reward.reservationId) &&
                Objects.equals(reservationName, reward.reservationName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, points, reservationId, reservationName, version, deleted);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeInt(this.points);
        dest.writeString(this.reservationId);
        dest.writeString(this.reservationName);
        dest.writeInt(this.version);
        dest.writeByte(this.deleted ? (byte) 1 : (byte) 0);
    }

    protected Reward(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.points = in.readInt();
        this.reservationId = in.readString();
        this.reservationName = in.readString();
        this.version = in.readInt();
        this.deleted = in.readByte() != 0;
    }

    public static final Creator<Reward> CREATOR = new Creator<Reward>() {
        @Override
        public Reward createFromParcel(Parcel source) {
            return new Reward(source);
        }

        @Override
        public Reward[] newArray(int size) {
            return new Reward[size];
        }
    };

    @Override
    public int compareTo(Object o) {
        return this.getName().compareTo(((Reward) o).getName());
    }
}
