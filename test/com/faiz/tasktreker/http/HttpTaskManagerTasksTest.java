package com.faiz.tasktreker.http;

import com.faiz.tasktreker.adapters.InstantAdapter;
import com.faiz.tasktreker.adapters.LocalDateTimeAdapter;

import com.google.gson.Gson;
import com.faiz.tasktreker.model.Task;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private static HttpTaskServer taskServer;
    private static Gson gson;

    private static final String TASK_BASE_URL = "http://localhost:8080/tasks";

    @BeforeAll
    static void startServer() {
        try {
            taskServer = new HttpTaskServer();
            taskServer.start();
            // Настраиваем gson с адаптерами
            gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void stopServer() {
        taskServer.stop();
    }

    @Test
    void shouldGetTasks() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(TASK_BASE_URL);

        // Создаем задачу
        Task task = new Task("Задача 1", "Описание задачи 1", LocalDateTime.now(), Duration.ofMinutes(10));

        // Добавляем задачу на сервер
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")  // Убедитесь, что тип данных установлен
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "Задача не была добавлена успешно."); // Проверяем, что задача добавлена.

            // Получаем задачи
            HttpRequest getRequest = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode(), "Получение задач не прошло успешно."); // Проверяем статус ответа на запрос GET.
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(1, arrayTasks.size(), "Количество задач не соответствует ожидаемому."); // Проверяем количество задач.

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Тест завершился ошибкой: " + e.getMessage());
        }
    }
}