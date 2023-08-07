package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Task;
import model.exception.ManagerIntersectionException;
import model.exception.ManagerValidateException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.OptionalInt;

public class TaskHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(Instant.class, new InstantAdapter());
        this.gson = gsonBuilder.create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final String method = exchange.getRequestMethod();
        final String path = exchange.getRequestURI().getPath();

        switch (method) {
            case "POST":
                InputStream inputStream = exchange.getRequestBody();
                String requestBody = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                if (isUpdate(requestBody)) {
                    Response updateResponse = updateTask(requestBody);

                    Headers headers = exchange.getResponseHeaders();
                    headers.set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(updateResponse.getCode(), 0);

                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(updateResponse.getResponse().getBytes());
                    } finally {
                        exchange.close();
                    }
                    break;
                } else {
                    Response createResponse = createTask(requestBody);

                    Headers headers = exchange.getResponseHeaders();
                    headers.set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(createResponse.getCode(), 0);

                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(createResponse.getResponse().getBytes());
                    } finally {
                        exchange.close();
                    }
                    break;
                }
            case "GET":
                Response getResponse = getTask(getIdFromPath(exchange));

                Headers headers1 = exchange.getResponseHeaders();
                headers1.set("Content-Type", "application/json");
                exchange.sendResponseHeaders(getResponse.getCode(), 0);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(getResponse.getResponse().getBytes());
                } finally {
                    exchange.close();
                }
                break;
            case "DELETE":
                Response deleteResponse = deleteTask(getIdFromPath(exchange));

                Headers headers2 = exchange.getResponseHeaders();
                headers2.set("Content-Type", "application/json");
                exchange.sendResponseHeaders(deleteResponse.getCode(), 0);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(deleteResponse.getResponse().getBytes());
                } finally {
                    exchange.close();
                }
        }
    }

    private Task getCreatedTask() {
        OptionalInt newTaskId = taskManager.getAllTasks().stream()
                .map(Task::getId)
                .mapToInt(id -> id)
                .max();
        return taskManager.getTask(newTaskId.getAsInt());
    }

    private int getIdFromPath(HttpExchange exchange) {
        return Integer.parseInt(exchange.getRequestURI().getQuery().split("=")[1]);
    }

    private boolean isUpdate(String requestBody) {
        return requestBody.contains("\"id\": ");
    }

    private Response createTask(String body) {
        if (body.isEmpty()) {
            return new Response(400, "Тело запроса не должно быть пустым.");
        } else {
            try {
                Task taskData = gson.fromJson(body, Task.class);
                taskManager.createTask(taskData);
                Task createdTask = getCreatedTask();
                return new Response(201, gson.toJson(createdTask));
            } catch (ManagerIntersectionException e) {
                return new Response(400, e.getMessage());
            }
        }
    }

    private Response updateTask(String requestBody) {
        try {
            Task task  = gson.fromJson(requestBody, Task.class);
            int taskId = task.getId();
            taskManager.updateTask(taskId, task);

            return new Response(200, gson.toJson(taskManager.getTask(taskId)));
        } catch (ManagerValidateException e) {
            return new Response(404, e.getMessage());
        }
    }

    private Response getTask(int id) {
        Task task = taskManager.getTask(id);

        if (task == null) {
            return new Response(404, String.format("Задача с id = %d не найдена.", id));
        } else {
            return new Response(200, gson.toJson(task));
        }
    }

    private Response deleteTask(int id) {
        Task task = taskManager.getTask(id);

        if (task == null) {
            return new Response(400, "Задача, которую вы хотите удалить не существует.");
        } else {
            taskManager.removeTaskById(id);
            return new Response(200, String.format("Задача с id = %d удалена.", id));
        }
    }
}
