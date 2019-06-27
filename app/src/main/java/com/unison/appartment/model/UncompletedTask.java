package com.unison.appartment.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.util.Objects;

/**
 * Classe che rappresenta un task da completare
 */
public class UncompletedTask implements Parcelable {

    private final static String ATTRIBUTE_CREATION_DATE = "creation-date";
    private final static String ATTRIBUTE_ASSIGNED_USER_ID = "assigned-user-id";
    private final static String ATTRIBUTE_ASSIGNED_USER_NAME = "assigned-user-name";

    @Exclude
    private String id;
    private String name;
    private String description;
    private int points;
    @PropertyName(ATTRIBUTE_CREATION_DATE)
    private long creationDate;
    @Nullable
    @PropertyName(ATTRIBUTE_ASSIGNED_USER_ID)
    private String assignedUserId;
    @Nullable
    @PropertyName(ATTRIBUTE_ASSIGNED_USER_NAME)
    private String assignedUserName;
    private boolean marked;

    // Costruttore vuoto richiesto da firebase
    public UncompletedTask() {}

    public UncompletedTask(String name, String description, int points) {
        this(name, description, points, System.currentTimeMillis());
    }

    public UncompletedTask(String name, String description, int points, long creationDate) {
        this.name = name;
        this.description = description;
        this.points = points;
        this.creationDate = creationDate;
    }

    public UncompletedTask(String id, String name, String description, int points, long creationDate, @Nullable String assignedUserId, @Nullable String assignedUserName, boolean marked) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.points = points;
        this.creationDate = creationDate;
        this.assignedUserId = assignedUserId;
        this.assignedUserName = assignedUserName;
        this.marked = marked;
    }

    public UncompletedTask(String name, String description, int points, long creationDate, @Nullable String assignedUserId, @Nullable String assignedUserName, boolean marked) {
        this.name = name;
        this.description = description;
        this.points = points;
        this.creationDate = creationDate;
        this.assignedUserId = assignedUserId;
        this.assignedUserName = assignedUserName;
        this.marked = marked;
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

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    @PropertyName(ATTRIBUTE_CREATION_DATE)
    public long getCreationDate() {
        return creationDate;
    }

    @PropertyName(ATTRIBUTE_CREATION_DATE)
    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    @Nullable
    @PropertyName(ATTRIBUTE_ASSIGNED_USER_ID)
    public String getAssignedUserId() {
        return assignedUserId;
    }

    @PropertyName(ATTRIBUTE_ASSIGNED_USER_ID)
    public void setAssignedUserId(@Nullable String assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    @Nullable
    @PropertyName(ATTRIBUTE_ASSIGNED_USER_NAME)
    public String getAssignedUserName() {
        return assignedUserName;
    }

    @PropertyName(ATTRIBUTE_ASSIGNED_USER_NAME)
    public void setAssignedUserName(@Nullable String assignedUserName) {
        this.assignedUserName = assignedUserName;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    @Exclude
    public boolean isAssigned() {
        return this.assignedUserId != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UncompletedTask that = (UncompletedTask) o;
        return points == that.points &&
                creationDate == that.creationDate &&
                marked == that.marked &&
                name.equals(that.name) &&
                description.equals(that.description) &&
                Objects.equals(assignedUserId, that.assignedUserId) &&
                Objects.equals(assignedUserName, that.assignedUserName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, points, creationDate, assignedUserId, assignedUserName, marked);
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
        dest.writeLong(this.creationDate);
        dest.writeString(this.assignedUserId);
        dest.writeString(this.assignedUserName);
        dest.writeByte(this.marked ? (byte) 1 : (byte) 0);
    }

    protected UncompletedTask(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.points = in.readInt();
        this.creationDate = in.readLong();
        this.assignedUserId = in.readString();
        this.assignedUserName = in.readString();
        this.marked = in.readByte() != 0;
    }

    public static final Parcelable.Creator<UncompletedTask> CREATOR = new Parcelable.Creator<UncompletedTask>() {
        @Override
        public UncompletedTask createFromParcel(Parcel source) {
            return new UncompletedTask(source);
        }

        @Override
        public UncompletedTask[] newArray(int size) {
            return new UncompletedTask[size];
        }
    };
}
