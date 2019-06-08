package com.unison.appartment.database;

public class DatabaseConstants {
    // Generale
    public final static String ROOT = "/";
    public final static String SEPARATOR = "/";

    // Post
    public final static String POSTS = "posts";
    public final static String POSTS_HOMENAME_POSTID_TIMESTAMP = "timestamp";

    // Case
    public final static String HOMES = "homes";
    public final static String HOMES_HOMENAME_PASSWORD = "password";

    // Utenti di una casa
    public final static String HOMEUSERS = "home-users";
    public final static String HOMEUSERS_HOMENAME_UID_CLAIMEDREWARDS = "claimed-rewards";
    public final static String HOMEUSERS_HOMENAME_UID_COMPLETEDTASKS = "completed-tasks";
    public final static String HOMEUSERS_HOMENAME_UID_POINTS = "points";
    public final static String HOMEUSERS_HOMENAME_UID_ROLE = "role";
    public final static String HOMEUSERS_HOMENAME_UID_TOTALEARNEDPOINTS = "total-earned-points";
    public final static String HOMEUSERS_HOMENAME_UID_TEXTPOSTS = "text-posts";
    public final static String HOMEUSERS_HOMENAME_UID_IMAGEPOSTS = "image-posts";
    public final static String HOMEUSERS_HOMENAME_UID_AUDIOPOSTS = "audio-posts";
    public final static String HOMEUSERS_HOMENAME_UID_REJECTEDTASKS = "rejected-tasks";

    // Premi
    public final static String REWARDS = "rewards";
    public static final String REWARDS_HOMENAME_REWARDID_NAME = "name";
    public static final String REWARDS_HOMENAME_REWARDID_RESERVATIONID = "reservation-id";
    public static final String REWARDS_HOMENAME_REWARDID_RESERVATIONNAME = "reservation-name";

    // UncompletedTask
    public final static String UNCOMPLETEDTASKS = "uncompleted-tasks";
    public final static String UNCOMPLETEDTASKS_HOMENAME_TASKID_ASSIGNEDUSERID = "assigned-user-id";
    public final static String UNCOMPLETEDTASKS_HOMENAME_TASKID_ASSIGNEDUSERNAME = "assigned-user-name";
    public final static String UNCOMPLETEDTASKS_HOMENAME_TASKID_CREATIONDATE = "creation-date";
    public final static String UNCOMPLETEDTASKS_HOMENAME_TASKID_MARKED = "marked";

    // CompletedTask
    public final static String COMPLETEDTASKS = "completed-tasks";
    public final static String COMPLETEDTASKS_HOMENAME_TASKID_LASTCOMPLETIONDATE = "last-completion-date";
    public final static String COMPLETEDTASKS_HOMENAME_TASKID_NAME = "name";

    // Completions
    public final static String COMPLETIONS = "completions";
    public final static String COMPLETIONS_HOMENAME_TASKID_COMPLETIONDATE = "completion-date";

    // Case di un utente
    public final static String USERHOMES = "user-homes";
    public final static String USERHOMES_UID_HOMENAME_ROLE = "role";

    // Utenti
    public final static String USERS = "users";
    public final static String USERS_UID_NAME = "name";
}
