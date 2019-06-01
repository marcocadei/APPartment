package com.unison.appartment.model;

import com.google.firebase.database.PropertyName;

public class Completion {

    private final static String ATTRIBUTE_COMPLETION_DATE = "completion-date";

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
}
