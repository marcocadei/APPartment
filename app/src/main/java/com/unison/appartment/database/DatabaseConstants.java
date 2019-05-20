package com.unison.appartment.database;

public class DatabaseConstants {
    // Generale
    public final static String ROOT = "/";
    public final static String SEPARATOR = "/";

    // Case
    public final static String HOMES = "homes";
    public final static String HOMES_HOMENAME = "%1$s";
    public final static String HOMES_HOMENAME_PASSWORD = "password";

    // Utenti di una casa
    public final static String HOMEUSERS = "home-users";
    public final static String HOMEUSERS_HOMENAME = "%1$s";
    public final static String HOMEUSERS_HOMENAME_UID = "%1$s";

    // UncompletedTask
    public final static String UNCOMPLETEDTASKS = "uncompleted-tasks";
    public final static String UNCOMPLETEDTASKS_HOMENAME_TASKID_CREATIONDATE = "creation-date";

    // Case di un utente
    public final static String USERHOMES = "user-homes";
    public final static String USERHOMES_UID = "%1$s";
    public final static String USERHOMES_UID_HOMENAME = "%1$s";

    // Utenti
    public final static String USERS = "users";
    public final static String USERS_UID = "%1$s";
    public final static String USERS_UID_NAME = "name";
}
