package com.unison.appartment.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Objects;

/**
 * Classe che rappresenta un task da completare
 */
public class Task implements Serializable {
    @Exclude
    private String id;
    private String name;
    private String description;
    private String creationDate;
    private int points;

    public Task(String name, String description, String creationDate, int points) {
        this.name = name;
        this.description = description;
        this.creationDate = creationDate;
        this.points = points;
    }

    public Task() {
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

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return points == task.points &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                Objects.equals(creationDate, task.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, creationDate, points);
    }

    // TODO da rimuovere
    /*    public static final List<Task> TASKS = new ArrayList<Task>(){
        {
            add(new Task("Aspirapolvere", "Passare aspirapolvere per tutta casa", "2019-29-04", 100));
            add(new Task("Piatti", "Lavare i piatti della cena", "2019-28-04", 30));
            add(new Task("Polveri", "Fare le polveri in camera dei ragazzi", "2019-30-04", 50));
        }
    };

    public static void addTask(int position, Task task) {
        TASKS.add(position, task);
    }*/
}
