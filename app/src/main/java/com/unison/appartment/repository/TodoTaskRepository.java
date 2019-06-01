package com.unison.appartment.repository;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
    // Riferimento al nodo home-users/home-name che mi serve per aggiornare le statistiche dell'utente
    // una volta completato un compito
    private DatabaseReference homeUserRef;
    // Riferimento al nodo completed-tasks/home-name/task-name che viene aggiornato quando si conferma
    // il completamento di un task
    private DatabaseReference completedTaskRef;
    // Riferimento al nodo completions usato per mantenere una cronologia
    private DatabaseReference completionsRef;
    // Riferimento al nodo del database a cui sono interessato
    private DatabaseReference uncompletedTasksRef;
    // Livedata che rappresenta i dati nel nodo del database considerato che vengono convertiti
    // tramite un Deserializer in ogetti di tipo UncompletedTask
    private FirebaseQueryLiveData liveData;
    private LiveData<List<UncompletedTask>> taskLiveData;

    public TodoTaskRepository() {
        // Riferimento al nodo root del database
        rootRef = FirebaseDatabase.getInstance().getReference();
        // Riferimento al nodo home-users/home-name che mi serve per aggiornare le statistiche dell'utente
        // una volta completato un compito
        homeUserRef = FirebaseDatabase.getInstance().getReference(DatabaseConstants.HOMEUSERS + DatabaseConstants.SEPARATOR +
                Appartment.getInstance().getHome().getName());
        // Riferimento al nodo completed-tasks/home-name/task-name che viene aggiornato quando si conferma
        // il completamento di un task
        completedTaskRef = FirebaseDatabase.getInstance().getReference(DatabaseConstants.COMPLETEDTASKS + DatabaseConstants.SEPARATOR +
                Appartment.getInstance().getHome().getName());
        // Riferimento al nodo completions usato per mantenere una cronologia
        completionsRef = FirebaseDatabase.getInstance().getReference(DatabaseConstants.COMPLETIONS + DatabaseConstants.SEPARATOR +
                Appartment.getInstance().getHome().getName());
        // Riferimento al nodo del database a cui sono interessato
        uncompletedTasksRef = FirebaseDatabase.getInstance().getReference(DatabaseConstants.UNCOMPLETEDTASKS +
                DatabaseConstants.SEPARATOR + Appartment.getInstance().getHome().getName());
        Query orderedTasks = uncompletedTasksRef.orderByChild(DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_CREATIONDATE);
        liveData = new FirebaseQueryLiveData(orderedTasks);
        taskLiveData = Transformations.map(liveData, new TodoTaskRepository.Deserializer());
    }

    @NonNull
    public LiveData<List<UncompletedTask>> getTaskLiveData() {
        return taskLiveData;
    }

    public void addTask(UncompletedTask newUncompletedTask) {
        String key = uncompletedTasksRef.push().getKey();
        newUncompletedTask.setId(key);
        newUncompletedTask.setCreationDate((-1) * newUncompletedTask.getCreationDate());
        uncompletedTasksRef.child(key).setValue(newUncompletedTask);
    }

    public void deleteTask(String id){
        uncompletedTasksRef.child(id).removeValue();
    }

    public void assignTask(String taskId, String userId, String userName) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_ASSIGNEDUSERID, userId);
        childUpdates.put(DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_ASSIGNEDUSERNAME, userName);
        uncompletedTasksRef.child(taskId).updateChildren(childUpdates);
    }

    public void removeAssignment(String taskId) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_ASSIGNEDUSERID, null);
        childUpdates.put(DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_ASSIGNEDUSERNAME, null);
        uncompletedTasksRef.child(taskId).updateChildren(childUpdates);
    }

    public void markTask(String taskId, String userId, String userName) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_ASSIGNEDUSERID, userId);
        childUpdates.put(DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_ASSIGNEDUSERNAME, userName);
        childUpdates.put(DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_MARKED, true);
        uncompletedTasksRef.child(taskId).updateChildren(childUpdates);
    }

    public void cancelCompletion(String taskId) {
        uncompletedTasksRef.child(taskId).child(DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_MARKED).setValue(false);
    }

    public void confirmCompletion(UncompletedTask task, String assignedUserId) {
        Map<String, Object> childUpdates;

        // Aggiornamento di uncompleted-tasks
        uncompletedTasksRef.child(task.getId()).removeValue();

        // Aggiornamento di home-users
        childUpdates = new HashMap<>();
        HomeUser homeUser = Appartment.getInstance().getHomeUser(assignedUserId);
        childUpdates.put(DatabaseConstants.HOMEUSERS_HOMENAME_UID_COMPLETEDTASKS, homeUser.getCompletedTasks() + 1);
        childUpdates.put(DatabaseConstants.HOMEUSERS_HOMENAME_UID_TOTALEARNEDPOINTS, homeUser.getTotalEarnedPoints() + task.getPoints());
        childUpdates.put(DatabaseConstants.HOMEUSERS_HOMENAME_UID_POINTS, homeUser.getPoints() + task.getPoints());
        homeUserRef.child(assignedUserId).updateChildren(childUpdates);

        // Aggiornamento dei completed-tasks
        childUpdates = new HashMap<>();
        long completionDate = System.currentTimeMillis();
        CompletedTask completedTask = new CompletedTask(task.getName(), task.getDescription(), task.getPoints(), (-1) * completionDate);
        childUpdates.put(task.getName(), completedTask);
        completedTaskRef.updateChildren(childUpdates);

        // Aggiornamento di completions
        childUpdates = new HashMap<>();
        Completion completion = new Completion(task.getAssignedUserName(), task.getPoints(), (-1) * completionDate);
        String key = completionsRef.child(task.getName()).push().getKey();
        childUpdates.put(key, completion);
        completionsRef.child(task.getName()).updateChildren(childUpdates);
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
