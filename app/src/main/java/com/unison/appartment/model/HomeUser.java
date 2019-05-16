package com.unison.appartment.model;

import java.util.Objects;

/**
 * Classe che rappresenta un membro di una casa
 */
public class HomeUser {

    private final static int DEFAULT_POINTS = 0;

    private String nickname;
    private int points;
    private long totalEarnedPoints;
    private int role;
    private int completedTasks;
    private int claimedRewards;
    private int earnedMoney;
    private int textPosts;
    private int audioPosts;
    private int imagePosts;
    private int rejectedTasks;
    private int unlockedAchievements;

    public HomeUser() {
    }

    public HomeUser(String nickname, int role) {
        this(nickname, DEFAULT_POINTS, DEFAULT_POINTS, role);
    }

    public HomeUser(String nickname, int points, long totalEarnedPoints, int role) {
        this.nickname = nickname;
        this.points = points;
        this.totalEarnedPoints = totalEarnedPoints;
        this.role = role;
    }

    public HomeUser(String nickname, int points, long totalEarnedPoints, int role, int completedTasks, int claimedRewards, int earnedMoney, int textPosts, int audioPosts, int imagePosts, int rejectedTasks, int unlockedAchievements) {
        this.nickname = nickname;
        this.points = points;
        this.totalEarnedPoints = totalEarnedPoints;
        this.role = role;
        this.completedTasks = completedTasks;
        this.claimedRewards = claimedRewards;
        this.earnedMoney = earnedMoney;
        this.textPosts = textPosts;
        this.audioPosts = audioPosts;
        this.imagePosts = imagePosts;
        this.rejectedTasks = rejectedTasks;
        this.unlockedAchievements = unlockedAchievements;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public long getTotalEarnedPoints() {
        return totalEarnedPoints;
    }

    public void setTotalEarnedPoints(long totalEarnedPoints) {
        this.totalEarnedPoints = totalEarnedPoints;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }

    public int getClaimedRewards() {
        return claimedRewards;
    }

    public void setClaimedRewards(int claimedRewards) {
        this.claimedRewards = claimedRewards;
    }

    public int getEarnedMoney() {
        return earnedMoney;
    }

    public void setEarnedMoney(int earnedMoney) {
        this.earnedMoney = earnedMoney;
    }

    public int getTextPosts() {
        return textPosts;
    }

    public void setTextPosts(int textPosts) {
        this.textPosts = textPosts;
    }

    public int getAudioPosts() {
        return audioPosts;
    }

    public void setAudioPosts(int audioPosts) {
        this.audioPosts = audioPosts;
    }

    public int getImagePosts() {
        return imagePosts;
    }

    public void setImagePosts(int imagePosts) {
        this.imagePosts = imagePosts;
    }

    public int getRejectedTasks() {
        return rejectedTasks;
    }

    public void setRejectedTasks(int rejectedTasks) {
        this.rejectedTasks = rejectedTasks;
    }

    public int getUnlockedAchievements() {
        return unlockedAchievements;
    }

    public void setUnlockedAchievements(int unlockedAchievements) {
        this.unlockedAchievements = unlockedAchievements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HomeUser homeUser = (HomeUser) o;
        return points == homeUser.points &&
                totalEarnedPoints == homeUser.totalEarnedPoints &&
                role == homeUser.role &&
                completedTasks == homeUser.completedTasks &&
                claimedRewards == homeUser.claimedRewards &&
                earnedMoney == homeUser.earnedMoney &&
                textPosts == homeUser.textPosts &&
                audioPosts == homeUser.audioPosts &&
                imagePosts == homeUser.imagePosts &&
                rejectedTasks == homeUser.rejectedTasks &&
                unlockedAchievements == homeUser.unlockedAchievements &&
                Objects.equals(nickname, homeUser.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname, points, totalEarnedPoints, role, completedTasks, claimedRewards, earnedMoney, textPosts, audioPosts, imagePosts, rejectedTasks, unlockedAchievements);
    }
}
