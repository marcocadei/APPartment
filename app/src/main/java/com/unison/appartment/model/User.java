package com.unison.appartment.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.unison.appartment.R;
import com.unison.appartment.state.MyApplication;
import com.unison.appartment.utils.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe che rappresenta un utente registrato all'applicazione, indipendente dalla/e casa/e in cui è presente
 */
public class User implements Parcelable {

    public final static int GENDER_MALE = 0;
    public final static int GENDER_FEMALE = 1;

    private String imageStoragePath;
    private String email;
    private String name;
    private String birthdate;
    private int gender;
    @Nullable
    private String image;

    public User (){
    }

    public User(String email, String name, String birthdate, int gender, @Nullable String image) {
        this.email = email;
        this.name = name;
        this.birthdate = birthdate;
        this.gender = gender;
        this.image = image;
    }

    public User(String email, String name, String birthdate, int gender, @Nullable String image, String imageStoragePath) {
        this.email = email;
        this.name = name;
        this.birthdate = birthdate;
        this.gender = gender;
        this.image = image;
        this.imageStoragePath = imageStoragePath;
    }

    public String getImageStoragePath() {
        return imageStoragePath;
    }

    public void setImageStoragePath(String imageStoragePath) {
        this.imageStoragePath = imageStoragePath;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imageStoragePath);
        dest.writeString(this.email);
        dest.writeString(this.name);
        dest.writeString(this.birthdate);
        dest.writeInt(this.gender);
        dest.writeString(this.image);
    }

    protected User(Parcel in) {
        this.imageStoragePath = in.readString();
        this.email = in.readString();
        this.name = in.readString();
        this.birthdate = in.readString();
        this.gender = in.readInt();
        this.image = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
