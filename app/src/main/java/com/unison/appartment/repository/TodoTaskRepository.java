package com.unison.appartment.repository;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.unison.appartment.database.FirebaseAuth;
import com.unison.appartment.model.CompletedTask;
import com.unison.appartment.model.Completion;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.state.Appartment;
import com.unison.appartment.database.DatabaseConstants;
import com.unison.appartment.livedata.FirebaseQueryLiveData;
import com.unison.appartment.model.UncompletedTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TodoTaskRepository {

    // Riferimento al nodo root del database
    private DatabaseReference rootRef;
    // Riferimento al nodo del database a cui sono interessato
    private DatabaseReference uncompletedTasksRef;
    // Livedata che rappresenta i dati nel nodo del database considerato che vengono convertiti
    // tramite un Deserializer in ogetti di tipo UncompletedTask
    private FirebaseQueryLiveData liveData;
    private LiveData<List<UncompletedTask>> taskLiveData;

    private MutableLiveData<Boolean> error;

    public TodoTaskRepository() {
        // Riferimento al nodo root del database
        rootRef = FirebaseDatabase.getInstance().getReference();
        // Riferimento al nodo del database a cui sono interessato
        uncompletedTasksRef = FirebaseDatabase.getInstance().getReference(DatabaseConstants.UNCOMPLETEDTASKS +
                DatabaseConstants.SEPARATOR + Appartment.getInstance().getHome().getName());
        Query orderedTasks = uncompletedTasksRef.orderByChild(DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_CREATIONDATE);
        liveData = new FirebaseQueryLiveData(orderedTasks);
        taskLiveData = Transformations.map(liveData, new TodoTaskRepository.Deserializer());

        error = new MutableLiveData<>();
    }

    @NonNull
    public LiveData<List<UncompletedTask>> getTaskLiveData() {
        return taskLiveData;
    }

    public LiveData<Boolean> getErrorLiveData() {
        return error;
    }

    public void addTask(UncompletedTask newUncompletedTask) {
        String key = uncompletedTasksRef.push().getKey();
        newUncompletedTask.setId(key);
        newUncompletedTask.setCreationDate((-1) * newUncompletedTask.getCreationDate());

        if (newUncompletedTask.isAssigned()) {
            // Se il task è stato già assegnato ad un utente in fase di creazione, bisogna
            // scrivere anche il riferimento in /home-users-refs.
            Map<String, Object> childUpdates = new HashMap<>();
            String homeName = Appartment.getInstance().getHome().getName();
            childUpdates.put(DatabaseConstants.UNCOMPLETEDTASKS + DatabaseConstants.SEPARATOR +
                            homeName + DatabaseConstants.SEPARATOR + key,
                    newUncompletedTask);
            childUpdates.put(DatabaseConstants.HOMEUSERSREFS + DatabaseConstants.SEPARATOR +
                            homeName + DatabaseConstants.SEPARATOR +
                            newUncompletedTask.getAssignedUserId() + DatabaseConstants.SEPARATOR +
                            DatabaseConstants.HOMEUSERSREFS_HOMENAME_UID_TASKS + DatabaseConstants.SEPARATOR + key,
                    true);
            rootRef.updateChildren(childUpdates);
        }
        else {
            uncompletedTasksRef.child(key).setValue(newUncompletedTask);
        }
    }

    public void editTask(UncompletedTask newUncompletedTask) {
        newUncompletedTask.setCreationDate((-1) * newUncompletedTask.getCreationDate());
        uncompletedTasksRef.child(newUncompletedTask.getId()).setValue(newUncompletedTask);
    }

    public void deleteTask(String id){
        uncompletedTasksRef.child(id).removeValue();
    }

    public void assignTask(String taskId, String userId, String userName) {
        String homeName = Appartment.getInstance().getHome().getName();
        String uncompletedTaskPath = DatabaseConstants.UNCOMPLETEDTASKS +
                DatabaseConstants.SEPARATOR + homeName + DatabaseConstants.SEPARATOR +
                taskId;
        String homeUserRefPath = DatabaseConstants.HOMEUSERSREFS + DatabaseConstants.SEPARATOR +
                homeName + DatabaseConstants.SEPARATOR + userId + DatabaseConstants.SEPARATOR +
                DatabaseConstants.HOMEUSERSREFS_HOMENAME_UID_TASKS + DatabaseConstants.SEPARATOR +
                taskId;

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(uncompletedTaskPath + DatabaseConstants.SEPARATOR + DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_ASSIGNEDUSERID, userId);
        childUpdates.put(uncompletedTaskPath + DatabaseConstants.SEPARATOR + DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_ASSIGNEDUSERNAME, userName);
        // Aggiungo l'id del task assegnato ai riferimenti associati all'utente
        childUpdates.put(homeUserRefPath, true);
        rootRef.updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // C'è un errore e quindi lo notifico, ma subito dopo l'errore non c'è più
                error.setValue(true);
                error.setValue(false);
            }
        });
    }

    public void removeAssignment(String taskId, String assignedUserId) {
        String homeName = Appartment.getInstance().getHome().getName();
        String uncompletedTaskPath = DatabaseConstants.UNCOMPLETEDTASKS +
                DatabaseConstants.SEPARATOR + homeName + DatabaseConstants.SEPARATOR +
                taskId;
        String homeUserRefPath = DatabaseConstants.HOMEUSERSREFS + DatabaseConstants.SEPARATOR +
                homeName + DatabaseConstants.SEPARATOR + assignedUserId + DatabaseConstants.SEPARATOR +
                DatabaseConstants.HOMEUSERSREFS_HOMENAME_UID_TASKS + DatabaseConstants.SEPARATOR +
                taskId;

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(uncompletedTaskPath + DatabaseConstants.SEPARATOR + DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_ASSIGNEDUSERID, null);
        childUpdates.put(uncompletedTaskPath + DatabaseConstants.SEPARATOR + DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_ASSIGNEDUSERNAME, null);
        // Tolgo l'id del task assegnato dai riferimenti associati all'utente
        childUpdates.put(homeUserRefPath, null);
        rootRef.updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // C'è un errore e quindi lo notifico, ma subito dopo l'errore non c'è più
                error.setValue(true);
                error.setValue(false);
            }
        });
    }

    public void switchAssignment(String taskId, String assignedUserId, String newAssignedUserId, String newAssignedUserName) {
        String homeName = Appartment.getInstance().getHome().getName();
        String uncompletedTaskPath = DatabaseConstants.UNCOMPLETEDTASKS +
                DatabaseConstants.SEPARATOR + homeName + DatabaseConstants.SEPARATOR +
                taskId;
        String oldHomeUserRefPath = DatabaseConstants.HOMEUSERSREFS + DatabaseConstants.SEPARATOR +
                homeName + DatabaseConstants.SEPARATOR + assignedUserId + DatabaseConstants.SEPARATOR +
                DatabaseConstants.HOMEUSERSREFS_HOMENAME_UID_TASKS + DatabaseConstants.SEPARATOR +
                taskId;
        String newHomeUserRefPath = DatabaseConstants.HOMEUSERSREFS + DatabaseConstants.SEPARATOR +
                homeName + DatabaseConstants.SEPARATOR + newAssignedUserId + DatabaseConstants.SEPARATOR +
                DatabaseConstants.HOMEUSERSREFS_HOMENAME_UID_TASKS + DatabaseConstants.SEPARATOR +
                taskId;

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(uncompletedTaskPath + DatabaseConstants.SEPARATOR + DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_ASSIGNEDUSERID, newAssignedUserId);
        childUpdates.put(uncompletedTaskPath + DatabaseConstants.SEPARATOR + DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_ASSIGNEDUSERNAME, newAssignedUserName);
        // Tolgo l'id del task assegnato dai riferimenti associati al vecchio utente
        childUpdates.put(oldHomeUserRefPath, null);
        // Aggiungo l'id del task assegnato ai riferimenti associati al nuovo utente
        childUpdates.put(newHomeUserRefPath, true);
        rootRef.updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // C'è un errore e quindi lo notifico, ma subito dopo l'errore non c'è più
                error.setValue(true);
                error.setValue(false);
            }
        });
    }

    public void markTask(String taskId, String userId, String userName) {
        String homeName = Appartment.getInstance().getHome().getName();
        String uncompletedTaskPath = DatabaseConstants.UNCOMPLETEDTASKS +
                DatabaseConstants.SEPARATOR + homeName + DatabaseConstants.SEPARATOR +
                taskId;
        String homeUserRefPath = DatabaseConstants.HOMEUSERSREFS + DatabaseConstants.SEPARATOR +
                homeName + DatabaseConstants.SEPARATOR + userId + DatabaseConstants.SEPARATOR +
                DatabaseConstants.HOMEUSERSREFS_HOMENAME_UID_TASKS + DatabaseConstants.SEPARATOR +
                taskId;

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(uncompletedTaskPath + DatabaseConstants.SEPARATOR + DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_ASSIGNEDUSERID, userId);
        childUpdates.put(uncompletedTaskPath + DatabaseConstants.SEPARATOR + DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_ASSIGNEDUSERNAME, userName);
        childUpdates.put(uncompletedTaskPath + DatabaseConstants.SEPARATOR + DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_MARKED, true);
        // Aggiungo l'id del task assegnato ai riferimenti associati all'utente
        childUpdates.put(homeUserRefPath, true);
        rootRef.updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // C'è un errore e quindi lo notifico, ma subito dopo l'errore non c'è più
                error.setValue(true);
                error.setValue(false);
            }
        });
    }

    public void cancelCompletion(String taskId, String userId) {
        Map<String, Object> childUpdates = new HashMap<>();

        String homeName = Appartment.getInstance().getHome().getName();
        String uncompletedTaskPath = DatabaseConstants.UNCOMPLETEDTASKS +
                DatabaseConstants.SEPARATOR + homeName;
        String homeUserPath = DatabaseConstants.HOMEUSERS + DatabaseConstants.SEPARATOR + homeName +
                DatabaseConstants.SEPARATOR + userId;

        childUpdates.put(homeUserPath + DatabaseConstants.SEPARATOR + DatabaseConstants.HOMEUSERS_HOMENAME_UID_REJECTEDTASKS,
                Appartment.getInstance().getHomeUser(userId).getRejectedTasks() + 1);
        // Il task non è più marked, ma rimane assegnato
        childUpdates.put(uncompletedTaskPath + DatabaseConstants.SEPARATOR + taskId + DatabaseConstants.SEPARATOR +
                DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_MARKED, false);
        rootRef.updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // C'è un errore e quindi lo notifico, ma subito dopo l'errore non c'è più
                error.setValue(true);
                error.setValue(false);
            }
        });
    }

    public void confirmCompletion(UncompletedTask task, String assignedUserId) {
        Map<String, Object> childUpdates = new HashMap<>();
        String homeName = Appartment.getInstance().getHome().getName();
        String uncompletedTaskPath = DatabaseConstants.UNCOMPLETEDTASKS + DatabaseConstants.SEPARATOR + homeName +
                DatabaseConstants.SEPARATOR + task.getId();
        String homeUserPath = DatabaseConstants.HOMEUSERS + DatabaseConstants.SEPARATOR + homeName +
                DatabaseConstants.SEPARATOR + assignedUserId;
        String completedTaskPath = DatabaseConstants.COMPLETEDTASKS + DatabaseConstants.SEPARATOR + homeName +
                DatabaseConstants.SEPARATOR + task.getName();
        String completionPath = DatabaseConstants.COMPLETIONS + DatabaseConstants.SEPARATOR + homeName +
                DatabaseConstants.SEPARATOR + task.getName();
        String homeUserRefPath = DatabaseConstants.HOMEUSERSREFS + DatabaseConstants.SEPARATOR +
                homeName + DatabaseConstants.SEPARATOR + assignedUserId + DatabaseConstants.SEPARATOR +
                DatabaseConstants.HOMEUSERSREFS_HOMENAME_UID_TASKS + DatabaseConstants.SEPARATOR +
                task.getId();

        // Aggiornamento di uncompleted-tasks
        childUpdates.put(uncompletedTaskPath, null);

        // Aggiornamento di home-users
        HomeUser homeUser = Appartment.getInstance().getHomeUser(assignedUserId);
        childUpdates.put(homeUserPath + DatabaseConstants.SEPARATOR + DatabaseConstants.HOMEUSERS_HOMENAME_UID_COMPLETEDTASKS,
                homeUser.getCompletedTasks() + 1);
        childUpdates.put(homeUserPath + DatabaseConstants.SEPARATOR + DatabaseConstants.HOMEUSERS_HOMENAME_UID_TOTALEARNEDPOINTS,
                Math.min(homeUser.getTotalEarnedPoints() + task.getPoints(), Long.MAX_VALUE));
        childUpdates.put(homeUserPath + DatabaseConstants.SEPARATOR + DatabaseConstants.HOMEUSERS_HOMENAME_UID_POINTS,
                Math.min(homeUser.getPoints() + task.getPoints(), HomeUser.MAX_POINTS));

        // Aggiornamento dei completed-tasks
        long completionDate = System.currentTimeMillis();
        CompletedTask completedTask = new CompletedTask(task.getName(), task.getDescription(), task.getPoints(), (-1) * completionDate);
        childUpdates.put(completedTaskPath, completedTask);

        // Aggiornamento di completions
        Completion completion = new Completion(Appartment.getInstance().getHomeUser(assignedUserId).getNickname(), task.getPoints(), (-1) * completionDate);
        String key = rootRef.child(completionPath).push().getKey();
        childUpdates.put(completionPath + DatabaseConstants.SEPARATOR + key, completion);

        // Aggiornamento del riferimento al task confermato
        childUpdates.put(homeUserRefPath, null);

        rootRef.updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // C'è un errore e quindi lo notifico, ma subito dopo l'errore non c'è più
                error.setValue(true);
                error.setValue(false);
            }
        });
    }

    private class Deserializer implements Function<DataSnapshot, List<UncompletedTask>> {
        @Override
        public List<UncompletedTask> apply(DataSnapshot dataSnapshot) {
            List<UncompletedTask> uncompletedTasks = new ArrayList<>();
            for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                UncompletedTask newUncompletedTask = taskSnapshot.getValue(UncompletedTask.class);
                newUncompletedTask.setId(taskSnapshot.getKey());
                newUncompletedTask.setCreationDate((-1) * newUncompletedTask.getCreationDate());
                uncompletedTasks.add(newUncompletedTask);
            }
            return uncompletedTasks;
        }
    }
}
