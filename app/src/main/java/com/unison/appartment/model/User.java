package com.unison.appartment.model;

import androidx.annotation.Nullable;

import com.unison.appartment.R;
import com.unison.appartment.state.MyApplication;
import com.unison.appartment.utils.DateUtils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Classe che rappresenta un utente registrato all'applicazione, indipendente dalla/e casa/e in cui è presente
 */
public class User implements Serializable {

    public final static int GENDER_MALE = 0;
    public final static int GENDER_FEMALE = 1;

    private String email;
    private String password;
    private String name;
    private String birthdate;
    private int gender;
    @Nullable
    private String image;

    public User (){
    }

    public User(String email, String password, String name, String birthdate, int gender, @Nullable String image) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthdate = birthdate;
        this.gender = gender;
        this.image = image;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public int getAge() throws ParseException {
        // Non si può fare di meglio in Java 7 purtroppo :/
        Date birth = DateUtils.parseDateWithStandardLocale(birthdate);
        Date today = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd", DateUtils.STANDARD_LOCALE);
        return (Integer.parseInt(formatter.format(today)) - Integer.parseInt(formatter.format(birth))) / 10000;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    @Nullable
    public String getImage() {
        return image;
    }

    public void setImage(@Nullable String image) {
        this.image = image;
    }

    public String getGenderString() {
        String[] genderValues = MyApplication.getAppContext().getResources().getStringArray(R.array.desc_users_uid_gender_values);
        return genderValues[this.getGender()];
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
