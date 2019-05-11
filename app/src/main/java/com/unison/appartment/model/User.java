package com.unison.appartment.model;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe che rappresenta un utente registrato all'applicazione, indipendente dalla/e casa/e in cui è presente
 */
public class User implements Serializable {

    private String email;
    private String password;
    private int age;
    private String gender;

    // TODO ancora da mettere le altre proprietà tipo numero di completed task
    private Uri image;

    // FIXME rifare il costruttore quando si è arrivati ad una struttura definitiva dell'utente
    public User(String email, String password, int age, String gender) {
        this.email = email;
        this.password = password;
        this.age = age;
        this.gender = gender;

        this.image = null; // TODO: piazza anche l'immagine

        // Attributi che devono essere impostati ad un valore di default alla creazione di un nuovo utente
        // (per es. anche numero di task completati ecc...)
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    // TODO da togliere

    /**
     * Metodi statici per gestire la lista di membri
     */
    private static List<User> userList = new ArrayList<User>(){
        {
//            add(new User("Gianluca", "g.roscigno@studenti.unibs.it", 22, "Maschio", "Leader", 10));
//            add(new User("Marco", "marcocadei@live.com", 24, "Maschio", "Leader", 100));
        }
    };

    public static void addUser(int position, User user) {
        userList.add(position, user);
    }

    public static void removeUser(int position) {
        userList.remove(position);
    }

    public static User getUser(int position) {
        return userList.get(position);
    }

    public static List<User> getUserList() {
        return userList;
    }

}
