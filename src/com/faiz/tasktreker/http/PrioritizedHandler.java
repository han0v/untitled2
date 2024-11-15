package com.faiz.tasktreker.http;

import com.faiz.tasktreker.service.TaskManager;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler{
    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleGet(httpExchange);
                break;
            default:
                sendNotFound(httpExchange, "Неподдерживаемый метод: " + method);
                break;
        }
    }

    private void handleGet(HttpExchange httpExchange) throws IOException {
        try {
            String jsonString = gson.toJson(taskManager.getPrioritizedTasks());
            System.out.println("GET PRIORITIZED: " + jsonString);
            sendText(httpExchange, jsonString);

        } catch (Exception e) {
            sendNotFound(httpExchange, "Ошибка при получении приоритезированных задач: " + e.getMessage());
        }
    }
}
