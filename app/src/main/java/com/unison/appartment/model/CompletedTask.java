package com.unison.appartment.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Objects;

/**
 * Classe che rappresenta un task da completare
 */
public class CompletedTask implements Serializable {

    @Exclude
    private String id;
    private String name;
    private String lastDescription;
    private int lastPoints;
    private long lastCompletionDate;

    public CompletedTask() {
    }

    public CompletedTask(String name, String lastDescription, int lastPoints, long lastCompletionDate) {
        this.name = name;
        this.lastDescription = lastDescription;
        this.lastPoints = lastPoints;
        this.lastCompletionDate = lastCompletionDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastDescription() {
        return lastDescription;
    }

    public void setLastDescription(String lastDescription) {
        this.lastDescription = lastDescription;
    }

    public int getLastPoints() {
        return lastPoints;
    }

    public void setLastPoints(int lastPoints) {
        this.lastPoints = lastPoints;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getLastCompletionDate() {
        return lastCompletionDate;
    }

    public void setLastCompletionDate(long lastCompletionDate) {
        this.lastCompletionDate = lastCompletionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompletedTask task = (CompletedTask) o;
        return lastPoints == task.lastPoints &&
                lastCompletionDate == task.lastCompletionDate &&
                name.equals(task.name) &&
                Objects.equals(lastDescription, task.lastDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, lastDescription, lastPoints, lastCompletionDate);
    }

}
