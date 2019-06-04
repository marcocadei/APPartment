package com.unison.appartment.model;

import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.unison.appartment.R;
import com.unison.appartment.state.MyApplication;
import com.unison.appartment.utils.DateUtils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe che rappresenta un utente registrato all'applicazione, indipendente dalla/e casa/e in cui è presente
 */
public class User implements Serializable {

    public final static int GENDER_MALE = 0;
    public final static int GENDER_FEMALE = 1;

    @Exclude
    private String id;
    private String email;
    private String password;
    private String name;
    private String birthdate;
    private int gender;
    @Nullable
    private String image;

    public User (){
    }

    public User(String id, String email, String password, String name, String birthdate, int gender, @Nullable String image) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthdate = birthdate;
        this.gender = gender;
        this.image = image;
    }

    public User(String email, String password, String name, String birthdate, int gender, @Nullable String image) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthdate = birthdate;
        this.gender = gender;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Exclude
    public int getAge() throws ParseException {
        // Non si può fare di meglio in Java 7 purtroppo :/
        Date birth = DateUtils.parseDateWithStandardLocale(birthdate);
        Date today = new Date();
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

    @Exclude
    public String getGenderString() {
        String[] genderValues = MyApplication.getAppContext().getResources().getStringArray(R.array.desc_users_uid_gender_values);
        return genderValues[this.getGender()];
    }
}
