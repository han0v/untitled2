package com.faiz.tasktreker.service;

import com.faiz.tasktreker.model.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
