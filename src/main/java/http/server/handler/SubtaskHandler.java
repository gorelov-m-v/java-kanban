package http.server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.server.adapter.InstantAdapter;
import http.server.response.Response;
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
import java.util.stream.Collectors;

public class SubtaskHandler extends HandlerHelper implements HttpHandler {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String CREATE_SCHEMA = "/create_subtask_schema.json";
    private static final String UPDATE_SCHEMA = "/update_subtask_schema.json";
    String[] paths = {"^/tasks/subtask/epic/\\?id=\\d+$", "^/tasks/subtask/?$",
            "^/tasks/subtask/\\?id=\\d+$", "^/tasks/subtask/epic/?$"};
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

         if (isCorrectPath(exchange, paths)) {
             switch (method) {
                 case "POST":
                     if (validateJson(requestBody, CREATE_SCHEMA)) {
                         response = createSubtask(exchange, requestBody);
                     } else {
                         response = new Response(400, getJsonError(requestBody, CREATE_SCHEMA));
                     }
                     break;
                 case "PUT":
                     if (validateJson(requestBody, UPDATE_SCHEMA)) {
                         response = updateSubtask(requestBody);
                     } else {
                         response = new Response(400, getJsonError(requestBody, CREATE_SCHEMA));
                     }
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

    private Subtask getCreatedSubtask() {
        OptionalInt newSubtaskId = taskManager.getAllSubtasks().stream()
                .map(Task::getId)
                .mapToInt(id -> id)
                .max();
        return taskManager.getSubtask(newSubtaskId.getAsInt());
    }

    private Response createSubtask(HttpExchange exchange, String requestBody) {
            try {
                Subtask subtaskData = gson.fromJson(requestBody, Subtask.class);
                int epicId = getIdFromPath(exchange);
                taskManager.createSubtask(subtaskData, epicId);

                Subtask createdSubtask = getCreatedSubtask();
                return new Response(201, gson.toJson(createdSubtask));
            } catch (ManagerIntersectionException e) {
                return new Response(400, gson.toJson(constructResponse(false, 405, e.getMessage())));
            }

    }

    private Response updateSubtask(String requestBody) {
            try {
                Subtask subtaskData = gson.fromJson(requestBody, Subtask.class);
                int subtaskId = subtaskData.getId();
                taskManager.updateSubtask(subtaskId, subtaskData);

                Subtask updatedSubtask = taskManager.getSubtask(subtaskId);
                return new Response(201, gson.toJson(updatedSubtask));
            } catch (ManagerIntersectionException |ManagerValidateException e) {
                return new Response(400, gson.toJson(constructResponse(false, 405, e.getMessage())));
            }
    }

    private Response getSubtask(int id) {
        Subtask subtask = taskManager.getSubtaskById(id);

        if (subtask == null) {
            return new Response(404, gson.toJson(constructResponse(
                    false, 404, String.format("Подзадача с id = %d не найдена.", id))));
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
            return new Response(404, gson.toJson(constructResponse(
                    false, 404, String.format("Подзадача с id = %d не найдена.", id))));
        } else {
            taskManager.removeSubtaskById(id);
            return new Response(200, gson.toJson(constructResponse(
                    true, 200, String.format("Подзадача с id = %d удалена.", id))));
        }
    }
}
