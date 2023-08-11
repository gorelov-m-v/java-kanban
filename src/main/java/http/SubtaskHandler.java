package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.exception.ManagerIntersectionException;
import model.exception.ManagerValidateException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.OptionalInt;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SubtaskHandler implements HttpHandler {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtaskHandler(TaskManager taskManager) {
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

        switch (method) {
            case "POST":
                response = createSubtask(exchange, requestBody);
                break;
            case "PUT":
                response = updateSubtask(requestBody);
                break;
            case "GET":
                if (exchange.getRequestURI().getPath().equals("/tasks/subtask/epic/")) {
                    response = getEpicSubtasks(exchange);
                } else {
                    response = getSubtask(getIdFromPath(exchange));
                }
                break;
            case "DELETE":
                if (isTotalDelete(exchange)) {
                    response = deleteAllSubtasks();
                } else {
                    response = deleteSubtask(getIdFromPath(exchange));
                }
                break;
            default:
                response = new Response(405, "Метод не поддерживается. Доступны: GET, POST, DELETE");
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

    private boolean isTotalDelete(HttpExchange exchange) {
        return exchange.getRequestURI().getQuery() == null;
    }

    private int getIdFromPath(HttpExchange exchange) {
        return Integer.parseInt(exchange.getRequestURI().getQuery().split("=")[1]);
    }

    private boolean isUpdate(String requestBody) {
        return requestBody.contains("\"id\": ");
    }

    private boolean isEpicSubtask(HttpExchange exchange) {
        String requestPath = exchange.getRequestURI().getPath();
        return Pattern.matches("^/tasks/subtask/epic/\\?id=\\d+$", requestPath);
    }

    private Subtask getCreatedSubtask() {
        OptionalInt newSubtaskId = taskManager.getAllSubtasks().stream()
                .map(Task::getId)
                .mapToInt(id -> id)
                .max();
        return taskManager.getSubtask(newSubtaskId.getAsInt());
    }

    private Response createSubtask(HttpExchange exchange, String requestBody) {
        if (requestBody.isEmpty()) {
            return new Response(400, "Тело запроса не должно быть пустым.");
        } else {
            try {
                Subtask subtaskData = gson.fromJson(requestBody, Subtask.class);
                int epicId = getIdFromPath(exchange);
                taskManager.createSubtask(subtaskData, epicId);

                Subtask createdSubtask = getCreatedSubtask();
                return new Response(201, gson.toJson(createdSubtask));
            } catch (ManagerIntersectionException e) {
                return new Response(400, e.getMessage());
            }
        }
    }

    private Response updateSubtask(String requestBody) {
        if (requestBody.isEmpty()) {
            return new Response(400, "Тело запроса не должно быть пустым.");
        } else {
            try {
                Subtask subtaskData = gson.fromJson(requestBody, Subtask.class);
                int subtaskId = subtaskData.getId();
                taskManager.updateSubtask(subtaskId, subtaskData);

                Subtask updatedSubtask = taskManager.getSubtask(subtaskId);
                return new Response(201, gson.toJson(updatedSubtask));
            } catch (ManagerIntersectionException |ManagerValidateException e) {
                return new Response(400, e.getMessage());
            }
        }
    }

    private Response getSubtask(int id) {
        Subtask subtask = taskManager.getSubtaskById(id);

        if (subtask == null) {
            return new Response(404, String.format("Подзадача с id = %d не найдена.", id));
        } else {
            return new Response(200, gson.toJson(subtask));
        }
    }

    private Response getEpicSubtasks(HttpExchange exchange) {
        Epic epic = taskManager.getEpic(getIdFromPath(exchange));
        
        if (epic == null) {
            return new Response(404, String.format("Эпика с id = %d не найден.", getIdFromPath(exchange)));
        } else {
            List<Subtask> subtasks = taskManager.getAllSubtasksFromEpic(epic.getId()).stream()
                    .map(taskManager::getSubtask)
                    .collect(Collectors.toList());
            return  new Response(200, gson.toJson(subtasks));
        }
    }

    private Response deleteAllSubtasks() {
        taskManager.removeAllSubtasks();
        return new Response(200, "Все подзадачи удалены.");
    }

    private Response deleteSubtask(int id) {
        Subtask subtask = taskManager.getSubtaskById(id);

        if (subtask == null) {
            return new Response(404, String.format("Подзадача с id = %d не найдена.", id));
        } else {
            taskManager.removeSubtaskById(id);
            return new Response(200, String.format("Подзадача с id = %d удалена.", id));
        }
    }
}
