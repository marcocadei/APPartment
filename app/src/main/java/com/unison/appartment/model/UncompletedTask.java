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
    private final static String ATTRIBUTE_ASSIGNED_USER = "assigned-user";

    @Exclude
    private String id;
    private String name;
    @Nullable
    private String description;
    private int points;
    @PropertyName(ATTRIBUTE_CREATION_DATE)
    private long creationDate;
    @Nullable
    @PropertyName(ATTRIBUTE_ASSIGNED_USER)
    private String assignedUser;
    private boolean marked;

    public UncompletedTask() {
    }

    public UncompletedTask(String name, String description, int points) {
        this(name, description, points, System.currentTimeMillis());
    }

    public UncompletedTask(String name, String description, int points, long creationDate) {
        this.name = name;
        this.description = description;
        this.points = points;
        this.creationDate = creationDate;
    }

    public UncompletedTask(String name, String description, int points, long creationDate, String assignedUser, boolean marked) {
        this(name, description, points, creationDate);
        this.assignedUser = assignedUser;
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
    @PropertyName(ATTRIBUTE_ASSIGNED_USER)
    public String getAssignedUser() {
        return assignedUser;
    }

    @PropertyName(ATTRIBUTE_ASSIGNED_USER)
    public void setAssignedUser(@Nullable String assignedUser) {
        this.assignedUser = assignedUser;
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
                Objects.equals(description, that.description) &&
                Objects.equals(assignedUser, that.assignedUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, points, creationDate, assignedUser, marked);
    }
}
