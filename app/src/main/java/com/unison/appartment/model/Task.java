package com.unison.appartment.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Task implements Serializable {

    private String name;
    private String description;
    private String deadline;
    private int points;

    public Task(String name, String description, String deadline, int points) {
        this.name = name;
        this.description = description;
        this.deadline = deadline;
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

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public static final List<Task> TASKS = new ArrayList<Task>(){
        {
            add(new Task("Aspirapolvere", "Passare aspirapolvere per tutta casa", "2019-29-04", 100));
            add(new Task("Piatti", "Lavare i piatti della cena", "2019-28-04", 30));
            add(new Task("Polveri", "Fare le polveri in camera dei ragazzi", "2019-30-04", 50));
        }
    };

    public static void addTask(int position, Task task) {
        TASKS.add(position, task);
    }
}
