package com.unison.appartment.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.util.Objects;

/**
 * Classe che rappresenta un membro di una casa
 */
public class HomeUser implements Parcelable {

    private final static String ATTRIBUTE_TOTAL_EARNED_POINTS = "total-earned-points";
    private final static String ATTRIBUTE_COMPLETED_TASKS = "completed-tasks";
    private final static String ATTRIBUTE_CLAIMED_REWARDS = "claimed-rewards";
    private final static String ATTRIBUTE_TEXT_POSTS = "text-posts";
    private final static String ATTRIBUTE_AUDIO_POSTS = "audio-posts";
    private final static String ATTRIBUTE_IMAGE_POSTS = "image-posts";
    private final static String ATTRIBUTE_REJECTED_TASKS = "rejected-tasks";

    public final static int MAX_POINTS = 999999999;
    private final static int DEFAULT_POINTS = 0;

    @Exclude
    private String userId;
    private String nickname;
    private int points;
    @PropertyName(ATTRIBUTE_TOTAL_EARNED_POINTS)
    private long totalEarnedPoints;
    private int role;
    @Nullable
    private String image;
    @PropertyName(ATTRIBUTE_COMPLETED_TASKS)
    private int completedTasks;
    @PropertyName(ATTRIBUTE_CLAIMED_REWARDS)
    private int claimedRewards;
    @PropertyName(ATTRIBUTE_TEXT_POSTS)
    private int textPosts;
    @PropertyName(ATTRIBUTE_AUDIO_POSTS)
    private int audioPosts;
    @PropertyName(ATTRIBUTE_IMAGE_POSTS)
    private int imagePosts;
    @PropertyName(ATTRIBUTE_REJECTED_TASKS)
    private int rejectedTasks;

    public HomeUser() {
    }

    public HomeUser(String nickname, int role, @Nullable String image) {
        this(nickname, DEFAULT_POINTS, DEFAULT_POINTS, role, image);
    }

    public HomeUser(String nickname, int points, long totalEarnedPoints, int role, @Nullable String image) {
        this.nickname = nickname;
        this.points = points;
        this.totalEarnedPoints = totalEarnedPoints;
        this.role = role;
        this.image = image;
    }

    public HomeUser(String nickname, int points, long totalEarnedPoints, int role, @Nullable String image, int completedTasks, int claimedRewards, int textPosts, int audioPosts, int imagePosts, int rejectedTasks) {
        this.nickname = nickname;
        this.points = points;
        this.totalEarnedPoints = totalEarnedPoints;
        this.role = role;
        this.image = image;
        this.completedTasks = completedTasks;
        this.claimedRewards = claimedRewards;
        this.textPosts = textPosts;
        this.audioPosts = audioPosts;
        this.imagePosts = imagePosts;
        this.rejectedTasks = rejectedTasks;
    }

    @Exclude
    public String getUserId() {
        return userId;
    }

    @Exclude
    public void setUserId(String userId) {
        this.userId = userId;
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

    @PropertyName(ATTRIBUTE_TOTAL_EARNED_POINTS)
    public long getTotalEarnedPoints() {
        return totalEarnedPoints;
    }

    @PropertyName(ATTRIBUTE_TOTAL_EARNED_POINTS)
    public void setTotalEarnedPoints(long totalEarnedPoints) {
        this.totalEarnedPoints = totalEarnedPoints;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    @Nullable
    public String getImage() {
        return image;
    }

    public void setImage(@Nullable String image) {
        this.image = image;
    }

    @PropertyName(ATTRIBUTE_COMPLETED_TASKS)
    public int getCompletedTasks() {
        return completedTasks;
    }

    @PropertyName(ATTRIBUTE_COMPLETED_TASKS)
    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }

    @PropertyName(ATTRIBUTE_CLAIMED_REWARDS)
    public int getClaimedRewards() {
        return claimedRewards;
    }

    @PropertyName(ATTRIBUTE_CLAIMED_REWARDS)
    public void setClaimedRewards(int claimedRewards) {
        this.claimedRewards = claimedRewards;
    }

    @PropertyName(ATTRIBUTE_TEXT_POSTS)
    public int getTextPosts() {
        return textPosts;
    }

    @PropertyName(ATTRIBUTE_TEXT_POSTS)
    public void setTextPosts(int textPosts) {
        this.textPosts = textPosts;
    }

    @PropertyName(ATTRIBUTE_AUDIO_POSTS)
    public int getAudioPosts() {
        return audioPosts;
    }

    @PropertyName(ATTRIBUTE_AUDIO_POSTS)
    public void setAudioPosts(int audioPosts) {
        this.audioPosts = audioPosts;
    }

    @PropertyName(ATTRIBUTE_IMAGE_POSTS)
    public int getImagePosts() {
        return imagePosts;
    }

    @PropertyName(ATTRIBUTE_IMAGE_POSTS)
    public void setImagePosts(int imagePosts) {
        this.imagePosts = imagePosts;
    }

    @PropertyName(ATTRIBUTE_REJECTED_TASKS)
    public int getRejectedTasks() {
        return rejectedTasks;
    }

    @PropertyName(ATTRIBUTE_REJECTED_TASKS)
    public void setRejectedTasks(int rejectedTasks) {
        this.rejectedTasks = rejectedTasks;
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
                textPosts == homeUser.textPosts &&
                audioPosts == homeUser.audioPosts &&
                imagePosts == homeUser.imagePosts &&
                rejectedTasks == homeUser.rejectedTasks &&
                nickname.equals(homeUser.nickname) &&
                Objects.equals(image, homeUser.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname, points, totalEarnedPoints, role, image, completedTasks, claimedRewards, textPosts, audioPosts, imagePosts, rejectedTasks);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.nickname);
        dest.writeInt(this.points);
        dest.writeLong(this.totalEarnedPoints);
        dest.writeInt(this.role);
        dest.writeString(this.image);
        dest.writeInt(this.completedTasks);
        dest.writeInt(this.claimedRewards);
        dest.writeInt(this.textPosts);
        dest.writeInt(this.audioPosts);
        dest.writeInt(this.imagePosts);
        dest.writeInt(this.rejectedTasks);
    }

    protected HomeUser(Parcel in) {
        this.userId = in.readString();
        this.nickname = in.readString();
        this.points = in.readInt();
        this.totalEarnedPoints = in.readLong();
        this.role = in.readInt();
        this.image = in.readString();
        this.completedTasks = in.readInt();
        this.claimedRewards = in.readInt();
        this.textPosts = in.readInt();
        this.audioPosts = in.readInt();
        this.imagePosts = in.readInt();
        this.rejectedTasks = in.readInt();
    }

    public static final Parcelable.Creator<HomeUser> CREATOR = new Parcelable.Creator<HomeUser>() {
        @Override
        public HomeUser createFromParcel(Parcel source) {
            return new HomeUser(source);
        }

        @Override
        public HomeUser[] newArray(int size) {
            return new HomeUser[size];
        }
    };
}
