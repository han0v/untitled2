package com.faiz.tasktreker.http;

import com.faiz.tasktreker.model.SubTask;
import com.faiz.tasktreker.model.Task;
import com.faiz.tasktreker.service.TaskManager;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Optional;

public class SubtaskHandler extends  BaseHttpHandler{
    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleGet(httpExchange);
                break;

            case "POST":
                handlePost(httpExchange);
                break;

            case "DELETE":
                handleDelete(httpExchange);
                break;

            default:
                sendNotFound(httpExchange, "Неподдерживаемый метод: " + method);
                break;
        }
    }

    private void handleGet(HttpExchange httpExchange) throws IOException {
        String query = httpExchange.getRequestURI().getQuery();
        if (query == null) {
            String jsonString = gson.toJson(taskManager.getSubTasks());
            System.out.println("GET SUBTASKS: " + jsonString);
            sendText(httpExchange, jsonString);
        } else {
            try {
                Integer id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                SubTask subTask = taskManager.getSubTaskById(id);
                if (subTask != null) {
                    String taskJson = gson.toJson(subTask);
                    sendText(httpExchange, taskJson);
                } else {
                    sendNotFound(httpExchange, "Подзадача не существует!");
                }
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                sendResponse(httpExchange, 400, "Неверный формат идентификатора эпика или запроса.");
            }
        }
    }

    private void handlePost(HttpExchange httpExchange) throws IOException {
        String bodyRequest = readText(httpExchange);
        try {
            SubTask subTask  = gson.fromJson(bodyRequest, SubTask.class);
            Integer id = subTask.getId();
            if (id == null) {
                taskManager.updateTask(subTask);
                sendResponse(httpExchange, 201, "Подзадача успешно обновлена.");
            } else {
                Optional<Task> existingTask = taskManager.getTasks().stream()
                        .filter(t -> t.getId().equals(id))
                        .findFirst();

                if (!existingTask.isPresent()) {
                    taskManager.createTask(subTask);
                    sendResponse(httpExchange, 201, "Подзадача успешно создана.");
                } else {
                    sendHasInteractions(httpExchange, "Подзадача с данным ID уже существует.");
                }
            }
        } catch (JsonSyntaxException e) {
            sendNotFound(httpExchange, "Oбъект не был найден;");
        }
    }

    private void handleDelete(HttpExchange httpExchange) throws IOException {
        String query = httpExchange.getRequestURI().getQuery();
        try {
            int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
            taskManager.delById(id);
            sendText(httpExchange, "Задача успешно удалена.");
        } catch (StringIndexOutOfBoundsException e) {
            sendResponse(httpExchange, 400, "Обязательный параметр id не может быть пустым");
        } catch (NumberFormatException e) {
            sendResponse(httpExchange, 400, "Неверный формат для параметра id");
        }
    }
}
