package com.unison.appartment.repository;


import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
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

    String uncompletedTaskPath = DatabaseConstants.UNCOMPLETEDTASKS +
            DatabaseConstants.SEPARATOR + Appartment.getInstance().getHome().getName();

    public TodoTaskRepository() {
        // Riferimento al nodo root del database
        rootRef = FirebaseDatabase.getInstance().getReference();
        // Riferimento al nodo del database a cui sono interessato
        uncompletedTasksRef = FirebaseDatabase.getInstance().getReference(uncompletedTaskPath);
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

    public void editTask(UncompletedTask newUncompletedTask) {
        newUncompletedTask.setCreationDate((-1) * newUncompletedTask.getCreationDate());
        uncompletedTasksRef.child(newUncompletedTask.getId()).setValue(newUncompletedTask);
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

    public void cancelCompletion(String taskId, String userId) {
        Map<String, Object> childUpdates = new HashMap<>();

        String homeName = Appartment.getInstance().getHome().getName();
        String homeUserPath = DatabaseConstants.HOMEUSERS + DatabaseConstants.SEPARATOR + homeName +
                DatabaseConstants.SEPARATOR + userId;

        childUpdates.put(homeUserPath + DatabaseConstants.SEPARATOR + DatabaseConstants.HOMEUSERS_HOMENAME_UID_REJECTEDTASKS,
                Appartment.getInstance().getHomeUser(userId).getRejectedTasks() + 1);
        // Il task non è più marked, ma rimane assegnato
        childUpdates.put(uncompletedTaskPath + DatabaseConstants.SEPARATOR + taskId + DatabaseConstants.SEPARATOR +
                DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_MARKED, false);
        rootRef.updateChildren(childUpdates);
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

        // Aggiornamento di uncompleted-tasks
        childUpdates.put(uncompletedTaskPath, null);

        // Aggiornamento di home-users
        HomeUser homeUser = Appartment.getInstance().getHomeUser(assignedUserId);
        childUpdates.put(homeUserPath + DatabaseConstants.SEPARATOR + DatabaseConstants.HOMEUSERS_HOMENAME_UID_COMPLETEDTASKS,
                homeUser.getCompletedTasks() + 1);
        childUpdates.put(homeUserPath + DatabaseConstants.SEPARATOR + DatabaseConstants.HOMEUSERS_HOMENAME_UID_TOTALEARNEDPOINTS,
                homeUser.getTotalEarnedPoints() + task.getPoints());
        childUpdates.put(homeUserPath + DatabaseConstants.SEPARATOR + DatabaseConstants.HOMEUSERS_HOMENAME_UID_POINTS,
                homeUser.getPoints() + task.getPoints());

        // Aggiornamento dei completed-tasks
        long completionDate = System.currentTimeMillis();
        CompletedTask completedTask = new CompletedTask(task.getName(), task.getDescription(), task.getPoints(), (-1) * completionDate);
        childUpdates.put(completedTaskPath, completedTask);

        // Aggiornamento di completions
        Completion completion = new Completion(Appartment.getInstance().getHomeUser(assignedUserId).getNickname(), task.getPoints(), (-1) * completionDate);
        String key = rootRef.child(completionPath).push().getKey();
        childUpdates.put(completionPath + DatabaseConstants.SEPARATOR + key, completion);

        rootRef.updateChildren(childUpdates);
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
