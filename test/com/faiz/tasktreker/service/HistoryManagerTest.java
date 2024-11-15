package com.faiz.tasktreker.service;

import com.faiz.tasktreker.model.Epic;
import com.faiz.tasktreker.model.SubTask;
import com.faiz.tasktreker.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class HistoryManagerTest {
    HistoryManager historyManager = new InMemoryHistoryManager();
    Task task;
    Epic epic;
    SubTask subTask;
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();

        task = new Task("Test description", "TestTask");
        task.setId(1);
        epic = new Epic("Test description", "TestEpic");
        epic.setId(2);
        subTask = new SubTask("Test description", "TestSubTask", 2, LocalDateTime.now(), Duration.ofMinutes(30));
        subTask.setId(3);
    }

    @Test
    void add() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(1, history.size());
    }

    @Test
    void add_HistorySize() {
        for (int i = 1; i <= 15; i++) {
            Task newTask = new Task("Test description " + i, "TestTask " + i);
            newTask.setId(i);
            historyManager.add(newTask);
        }

        final List<Task> history = historyManager.getHistory();
        assertEquals(15, history.size());
    }

    @Test
    void remove_TaskFromHistory() {
        historyManager.add(task);
        historyManager.remove(task.getId());

        final List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size()); // Проверка, что история пуста после удаления задачи.
    }

    @Test
    void add_Duplicates() {
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);

        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size()); // Проверка, что история содержит только одну запись
        assertEquals(task, history.get(0)); // Проверка, что это именно та задача, которую добавляли
    }

    @Test
    void remove_TaskAndEpic() {
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.delAll();

        final List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size()); // Проверка, что история пуста после удаления и задачи и эпика
    }

    @Test
    void remove_TaskAndSubTaskFromHistory() {

        historyManager.add(task);
        historyManager.add(subTask);

        historyManager.remove(subTask.getId());
        historyManager.remove(task.getId());

        final List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size()); // Проверка, что история пуста после удаления и задачи и подзадачи
    }

    @Test
    void removeFromHead() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);
        historyManager.remove(task.getId());
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(2, history.size(), "Задача не удалена");
    }

    @Test
    void removeFromMiddle() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);
        historyManager.remove(epic.getId());
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(2, history.size(), "Задача не удалена");
    }

    @Test
    void removeFromTail() {
        historyManager.add(epic);
        historyManager.add(subTask);
        historyManager.add(task);
        historyManager.remove(task.getId());
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(2, history.size(), "Задача не удалена");
    }

}