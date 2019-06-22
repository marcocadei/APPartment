package com.unison.appartment.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.Objects;

/**
 * Classe che rappresenta un task da completare
 */
public class CompletedTask implements Parcelable {

    private final static String ATTRIBUTE_LAST_DESCRIPTION = "last-description";
    private final static String ATTRIBUTE_LAST_POINTS = "last-points";
    private final static String ATTRIBUTE_LAST_COMPLETION_DATE = "last-completion-date";

    @Exclude
    private String id;
    private String name;
    @PropertyName(ATTRIBUTE_LAST_DESCRIPTION)
    private String lastDescription;
    @PropertyName(ATTRIBUTE_LAST_POINTS)
    private int lastPoints;
    @PropertyName(ATTRIBUTE_LAST_COMPLETION_DATE)
    private long lastCompletionDate;

    public CompletedTask() {
    }

    public CompletedTask(String name, String lastDescription, int lastPoints, long lastCompletionDate) {
        this.name = name;
        this.lastDescription = lastDescription;
        this.lastPoints = lastPoints;
        this.lastCompletionDate = lastCompletionDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @PropertyName(ATTRIBUTE_LAST_DESCRIPTION)
    public String getLastDescription() {
        return lastDescription;
    }

    @PropertyName(ATTRIBUTE_LAST_DESCRIPTION)
    public void setLastDescription(String lastDescription) {
        this.lastDescription = lastDescription;
    }

    @PropertyName(ATTRIBUTE_LAST_POINTS)
    public int getLastPoints() {
        return lastPoints;
    }

    @PropertyName(ATTRIBUTE_LAST_POINTS)
    public void setLastPoints(int lastPoints) {
        this.lastPoints = lastPoints;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    @PropertyName(ATTRIBUTE_LAST_COMPLETION_DATE)
    public long getLastCompletionDate() {
        return lastCompletionDate;
    }

    @PropertyName(ATTRIBUTE_LAST_COMPLETION_DATE)
    public void setLastCompletionDate(long lastCompletionDate) {
        this.lastCompletionDate = lastCompletionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompletedTask task = (CompletedTask) o;
        return lastPoints == task.lastPoints &&
                lastCompletionDate == task.lastCompletionDate &&
                name.equals(task.name) &&
                Objects.equals(lastDescription, task.lastDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, lastDescription, lastPoints, lastCompletionDate);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.lastDescription);
        dest.writeInt(this.lastPoints);
        dest.writeLong(this.lastCompletionDate);
    }

    protected CompletedTask(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.lastDescription = in.readString();
        this.lastPoints = in.readInt();
        this.lastCompletionDate = in.readLong();
    }

    public static final Parcelable.Creator<CompletedTask> CREATOR = new Parcelable.Creator<CompletedTask>() {
        @Override
        public CompletedTask createFromParcel(Parcel source) {
            return new CompletedTask(source);
        }

        @Override
        public CompletedTask[] newArray(int size) {
            return new CompletedTask[size];
        }
    };
}
