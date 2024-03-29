package http.server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.server.adapter.InstantAdapter;
import http.server.response.Response;
import manager.TaskManager;
import model.Task;
import model.exception.ManagerIntersectionException;
import model.exception.ManagerValidateException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.OptionalInt;

public class TaskHandler extends HandlerHelper implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String CREATE_SCHEMA = "/create_task_schema.json";
    private static final String UPDATE_SCHEMA = "/update_task_schema.json";
    String[] paths = {"^/tasks/task/\\?id=\\d+$", "^/tasks/task/?$"};
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
        Response response;

        InputStream inputStream = exchange.getRequestBody();
        String requestBody = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        if (isCorrectPath(exchange, paths)) {
            switch (method) {
                case "POST":
                    if (validateJson(requestBody, CREATE_SCHEMA)) {
                        response = createTask(requestBody);
                    } else {
                        response = new Response(400, getJsonError(requestBody, CREATE_SCHEMA));
                    }
                    break;
                case "PUT":
                    if (validateJson(requestBody, UPDATE_SCHEMA)) {
                        response = updateTask(requestBody);
                    } else {
                        response = new Response(400, getJsonError(requestBody, UPDATE_SCHEMA));
                    }
                    break;
                case "GET":
                    if (exchange.getRequestURI().getQuery() != null) {
                        response = getTask(getIdFromPath(exchange));
                    } else {
                        response = new Response(400, gson.toJson(constructResponse(false, 400,
                                "Данный эндпоинт не реализован.")));
                    }
                    break;
                case "DELETE":
                    if (isTotalDelete(exchange)) {
                        response = deleteAllTasks();
                    } else {
                        response = deleteTask(getIdFromPath(exchange));
                    }
                    break;
                default:
                    response = new Response(405, gson.toJson(constructResponse(false, 405,
                            "Метод не поддерживается. Доступны: GET, POST, DELETE, PUT")));
            }
        } else {
            response = new Response(405, gson.toJson(constructResponse(false, 405,
                    "Данный эндпоинт не реализован.")));
        }

        Headers headers2 = exchange.getResponseHeaders();
        headers2.set("Content-Type", "application/json");
        exchange.sendResponseHeaders(response.getCode(), 0);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getResponse().getBytes());
        } finally {
            exchange.close();
        }
    }

    private Task getCreatedTask() {
        OptionalInt newTaskId = taskManager.getAllTasks().stream()
                .map(Task::getId)
                .mapToInt(id -> id)
                .max();
        return taskManager.getTask(newTaskId.getAsInt());
    }

    public Response createTask(String body) {
        try {
            Task taskData = gson.fromJson(body, Task.class);
            taskManager.createTask(taskData);
            Task createdTask = getCreatedTask();
            return new Response(201, gson.toJson(createdTask));
        } catch (ManagerIntersectionException e) {
            return new Response(400, gson.toJson(constructResponse(false, 405, e.getMessage())));
        }
    }

    private Response updateTask(String requestBody) {
        try {
            Task task = gson.fromJson(requestBody, Task.class);
            int taskId = task.getId();
            taskManager.updateTask(taskId, task);
            return new Response(200, gson.toJson(taskManager.getTask(taskId)));
        } catch (ManagerValidateException e) {
            return new Response(404, gson.toJson(constructResponse(false, 405, e.getMessage())));
        }
    }

    private Response getTask(int id) {
        Task task = taskManager.getTaskById(id);

        if (task == null) {
            return new Response(404, gson.toJson(constructResponse(
                    false, 404, String.format("Задача с id = %d не найдена.", id))));
        } else {
            return new Response(200, gson.toJson(task));
        }
    }

    private Response deleteTask(int id) {
        Task task = taskManager.getTask(id);

        if (task == null) {
            return new Response(404, gson.toJson(constructResponse(
                    false, 404, String.format("Задача с id = %d не найдена.", id))));
        } else {
            taskManager.removeTaskById(id);
            return new Response(200, gson.toJson(constructResponse(
                    true, 200, String.format("Задача с id = %d удалена.", id))));
        }
    }

    private Response deleteAllTasks() {
        taskManager.removeAllTasks();
        return new Response(200, gson.toJson(constructResponse(
                true, 200, "Все задачи удалены.")));
    }
}
