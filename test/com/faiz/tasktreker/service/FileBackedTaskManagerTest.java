package com.faiz.tasktreker.service;

import com.faiz.tasktreker.model.Epic;
import com.faiz.tasktreker.model.SubTask;
import com.faiz.tasktreker.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    private FileBackedTaskManager manager;
    private File tempFile;

    @BeforeEach
    public void setUp() throws IOException {
        // Создание временного файла для тестов
        tempFile = new File("data.csv");
        // Удаляем файл, если он существует, чтобы начать с чистого состояния
        if (tempFile.exists()) {
            tempFile.delete();
        }
        manager = new FileBackedTaskManager(tempFile.getAbsolutePath());
    }

    @Test
    public void testSaveAndLoadEmptyFile() throws IOException {
        if (!tempFile.exists()) {
            tempFile.createNewFile();
        }

        // Загрузка из пустого файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertNotNull(loadedManager, "Загруженный менеджер не должен быть null");
        assertTrue(loadedManager.getTasks().isEmpty(), "Задачи должны быть пустыми");
        assertTrue(loadedManager.getSubTasks().isEmpty(), "Подзадачи должны быть пустыми");
        assertTrue(loadedManager.getEpics().isEmpty(), "Эпики должны быть пустыми");
    }


    @Test
    public void testSaveAndLoadTasks() throws IOException {
        // Создание задач и эпиков
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        manager.createTask(task1);
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        manager.createTask(task2);
        Epic epic = new Epic("Эпик", "Описание эпика");
        manager.createEpic(epic);
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", epic.getId(), LocalDateTime.now(), Duration.ofMinutes(30));
        manager.createSubTask(subTask1);

        // Загрузка данных из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверка количества задач
        assertEquals(manager.getTasks().size(), loadedManager.getTasks().size(), "Количество задач должно совпадать");
        assertEquals(manager.getSubTasks().size(), loadedManager.getSubTasks().size(), "Количество подзадач должно совпадать");
        assertEquals(manager.getEpics().size(), loadedManager.getEpics().size(), "Количество эпиков должно совпадать");

        // Проверка содержимого
        assertTrue(loadedManager.getTasks().equals(manager.getTasks()), "Задачи должны совпадать");
        assertTrue(loadedManager.getSubTasks().equals(manager.getSubTasks()), "Подзадачи должны совпадать");

        // Вывод для проверки
        System.out.println("Проверка задач: " + loadedManager.getTasks().equals(manager.getTasks()));
        System.out.println("Проверка подзадач: " + loadedManager.getSubTasks().equals(manager.getSubTasks()));
        System.out.println("Проверка эпиков: " + loadedManager.getEpics().equals(manager.getEpics()));
    }
}