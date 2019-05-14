package com.unison.appartment.model;

public class HomeUser {

    private final static int DEFAULT_POINTS = 0;

    private String nickname;
    private int points;

    public HomeUser(String nickname) {
        this(nickname, DEFAULT_POINTS);
    }

    public HomeUser(String nickname, int points) {
        this.nickname = nickname;
        this.points = points;
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
}
