package com.unison.appartment.model;

import java.util.Date;

public class UncompletedTask {
    private String name;
    private String description;
    private int points;
    private Date creationDate;
    private String assignedUser;
    private boolean markedAsCompleted;


    public UncompletedTask(String name, String description, int points, Date creationDate, String assignedUser, boolean markedAsCompleted) {
        this.name = name;
        this.description = description;
        this.points = points;
        this.creationDate = creationDate;
        this.assignedUser = assignedUser;
        this.markedAsCompleted = markedAsCompleted;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(String assignedUser) {
        this.assignedUser = assignedUser;
    }

    public boolean isMarkedAsCompleted() {
        return markedAsCompleted;
    }

    public void setMarkedAsCompleted(boolean markedAsCompleted) {
        this.markedAsCompleted = markedAsCompleted;
    }
}
