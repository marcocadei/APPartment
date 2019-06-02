package com.unison.appartment.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.util.Objects;

public class Completion {

    private final static String ATTRIBUTE_COMPLETION_DATE = "completion-date";

    @Exclude
    private String id;
    private String user;
    private int points;
    @PropertyName(ATTRIBUTE_COMPLETION_DATE)
    private long completionDate;

    public Completion() {
    }

    public Completion(String user, int points, long completionDate) {
        this.user = user;
        this.points = points;
        this.completionDate = completionDate;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @PropertyName(ATTRIBUTE_COMPLETION_DATE)
    public long getCompletionDate() {
        return completionDate;
    }

    @PropertyName(ATTRIBUTE_COMPLETION_DATE)
    public void setCompletionDate(long completionDate) {
        this.completionDate = completionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Completion that = (Completion) o;
        return points == that.points &&
                completionDate == that.completionDate &&
                user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, points, completionDate);
    }
}
