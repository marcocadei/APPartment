package com.unison.appartment.model;

import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe che rappresenta un premio da reclamare
 */
public class Reward implements Serializable {

    private static List<Reward> rewardsList = new ArrayList<>();

    private String name;
    @Nullable
    private String description;
    private int points;
    @Nullable
    private String reservation;

    public Reward() {}

    public Reward(String name, String description, int points) {
        this.name = name;
        this.description = description;
        this.points = points;
    }

    public Reward(String name, String description, int points, String reservation) {
        this(name, description, points);
        this.reservation = reservation;
    }

    @Nullable
    public String getReservation() {
        return reservation;
    }

    public void setReservation(@Nullable String reservation) {
        this.reservation = reservation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
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

    @Exclude
    public boolean isRequested() {
        return this.reservation != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reward reward = (Reward) o;
        return points == reward.points &&
                name.equals(reward.name) &&
                Objects.equals(description, reward.description) &&
                Objects.equals(reservation, reward.reservation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, points, reservation);
    }
}
