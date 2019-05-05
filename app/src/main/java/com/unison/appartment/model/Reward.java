package com.unison.appartment.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// FIXME Eliminare metodi che non servono
public class Reward implements Serializable {

    private static List<Reward> rewardsList = new ArrayList<>();

    private String name;

    private String description;

    private int points;

    private boolean requested;

    public Reward(String name, String description, int points) {
        this.name = name;
        this.description = description;
        this.points = points;
        this.requested = false;
    }

    public Reward(String name, int points) {
        this.name = name;
        this.points = points;
        this.requested = false;
    }

    public boolean isRequested() {
        return requested;
    }

    public void setRequested(boolean requested) {
        this.requested = requested;
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

    public static void addReward(int index, Reward reward) {
        rewardsList.add(index, reward);
    }

    public static void addReward(Reward reward) {
        rewardsList.add(reward);
    }

    public static void removeReward(int position) {
        rewardsList.remove(position);
    }

    public static Reward getReward(int position) {
        return rewardsList.get(position);
    }

    public static List<Reward> getRewardsList() {
        return rewardsList;
    }

}
