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
import model.Task;
import model.exception.ManagerValidateException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.OptionalInt;

public class EpicHandler extends HandlerHelper implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String CREATE_SCHEMA = "/create_epic_schema.json";
    private static final String UPDATE_SCHEMA = "/update_epic_schema.json";
    String[] paths = {"^/tasks/epic/\\?id=\\d+$", "^/tasks/epic/?$"};
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager) {
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
                        response = createEpic(requestBody);
                    } else {
                        response = new Response(400, getJsonError(requestBody, CREATE_SCHEMA));
                    }
                    break;
                case "PUT":
                    if (validateJson(requestBody, UPDATE_SCHEMA)) {
                        response = updateEpic(requestBody);
                    } else {
                        response = new Response(400, getJsonError(requestBody, UPDATE_SCHEMA));
                    }
                    break;
                case "GET":
                    if (exchange.getRequestURI().getPath() != null) {
                        response = getEpic(getIdFromPath(exchange));
                    } else {
                        response = new Response(400, gson.toJson(constructResponse(false, 400,
                                "Данный эндпоинт не реализован.")));
                    }
                    break;
                case "DELETE":
                    if (isTotalDelete(exchange)) {
                        response = deleteAllEpics();
                    } else {
                        response = deleteEpic(getIdFromPath(exchange));
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

    private Epic getCreatedEpic() {
        OptionalInt newEpicId = taskManager.getEpics().stream()
                .map(Task::getId)
                .mapToInt(id -> id)
                .max();
        return taskManager.getEpic(newEpicId.getAsInt());
    }

    private Response createEpic(String requestBody) {
            Epic epicData = gson.fromJson(requestBody, Epic.class);
            taskManager.createEpic(epicData);
            Epic createdEpic = getCreatedEpic();
            return new Response(201, gson.toJson(createdEpic));
    }

    private Response updateEpic(String requestBody) {
        try {
            Epic epic  = gson.fromJson(requestBody, Epic.class);
            int epicId = epic.getId();
            taskManager.updateEpic(epicId, epic);
            return new Response(200, gson.toJson(taskManager.getEpic(epicId)));
        } catch (ManagerValidateException e) {
            return new Response(404, gson.toJson(constructResponse(false, 405, e.getMessage())));
        }
    }

    private Response getEpic(int id) {
        Epic epic = taskManager.getEpicById(id);

        if (epic == null) {
            return new Response(404, gson.toJson(constructResponse(
                    false, 404, String.format("Эпик с id = %d не найден.", id))));
        } else {
            return new Response(200, gson.toJson(epic));
        }
    }

    private Response deleteEpic(int id) {
        Epic epic = taskManager.getEpic(id);

        if (epic == null) {
            return new Response(404, gson.toJson(constructResponse(
                    false, 404, String.format("Эпик с id = %d не найден.", id))));
        } else {
            taskManager.removeEpicById(id);
            return new Response(200, String.format("Эпик с id = %d удален.", id));
        }
    }

    private Response deleteAllEpics() {
        taskManager.removeAllEpics();
        return new Response(200, gson.toJson(constructResponse(
                true, 200, "Все задачи удалены.")));
    }
}
