package com.faiz.tasktreker.service;

import com.faiz.tasktreker.model.Epic;
import com.faiz.tasktreker.model.SubTask;
import com.faiz.tasktreker.model.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {
    Task getTaskById(int id);

    Epic getEpicById(int id);

    SubTask getSubTaskById(int id);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<SubTask> getSubTasks();

    void delAll();

    void delAllTasks();

    void delAllEpics();

    void delAllSubTasks();

    Task getById(int id);

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    SubTask createSubTask(SubTask subTask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask sub);

    void delById(int id);

    List<SubTask> getSubTaskList(int epicId);

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();
}
