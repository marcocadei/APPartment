package com.unison.appartment.model;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Member implements Serializable {

    private String email;
    private String name;
    private int age;
    private String gender;
    private String role;
    private int points = 0;
    private Uri image;

    // TODO: piazza anche l'immagine
    public Member(String email, String name, int age, String gender, String role, int points) {
        this.email = email;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.role = role;
        this.points = points;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    /**
     * Metodi statici per gestire la lista di membri
     */
    private static List<Member> memberList = new ArrayList<Member>(){
        {
            add(new Member("Gianluca", "g.roscigno@studenti.unibs.it", 22, "Maschio", "Leader", 10));
            add(new Member("Marco", "marcocadei@live.com", 24, "Maschio", "Leader", 100));
        }
    };

    public static void addMember(int position, Member member) {
        memberList.add(position, member);
    }

    public static void removeMember(int position) {
        memberList.remove(position);
    }

    public static Member getMember(int position) {
        return memberList.get(position);
    }

    public static List<Member> getMemberList() {
        return memberList;
    }

}
