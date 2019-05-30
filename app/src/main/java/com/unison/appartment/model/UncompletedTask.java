package com.unison.appartment.model;

import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.Objects;

/**
 * Classe che rappresenta un task da completare
 */
public class UncompletedTask implements Serializable {

    private final static String ATTRIBUTE_CREATION_DATE = "creation-date";
    private final static String ATTRIBUTE_ASSIGNED_USER_ID = "assigned-user-id";
    private final static String ATTRIBUTE_ASSIGNED_USER_NAME = "assigned-user-name";

    @Exclude
    private String id;
    private String name;
    @Nullable
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

    public UncompletedTask(String name, @Nullable String description, int points, long creationDate, @Nullable String assignedUserId, @Nullable String assignedUserName, boolean marked) {
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
}
