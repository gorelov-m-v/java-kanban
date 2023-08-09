package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
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

public class EpicHandler implements HttpHandler {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
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

        switch (method) {
            case "POST":
                if (isUpdate(requestBody)) {
                    response = updateEpic(requestBody);
                } else {
                    response = createEpic(requestBody);
                }
                break;
            case "GET":
                response = getEpic(getIdFromPath(exchange));
                break;
            case "DELETE":
                if (isTotalDelete(exchange)) {
                    response = deleteAllEpics();
                } else {
                    response = deleteEpic(getIdFromPath(exchange));
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

    private Epic getCreatedEpic() {
        OptionalInt newEpicId = taskManager.getAllEpics().stream()
                .map(Task::getId)
                .mapToInt(id -> id)
                .max();
        return taskManager.getEpic(newEpicId.getAsInt());
    }

    private Response createEpic(String requestBody) {
        if (requestBody.isEmpty()) {
            return new Response(400, "Тело запроса не должно быть пустым.");
        } else {
            Epic epicData = gson.fromJson(requestBody, Epic.class);
            taskManager.createTask(epicData);
            Task createdEpic = getCreatedEpic();
            return new Response(201, gson.toJson(createdEpic));
        }
    }

    private Response updateEpic(String requestBody) {
        try {
            Epic epic  = gson.fromJson(requestBody, Epic.class);
            int epicId = epic.getId();
            taskManager.updateTask(epicId, epic);

            return new Response(200, gson.toJson(taskManager.getEpic(epicId)));
        } catch (ManagerValidateException e) {
            return new Response(404, e.getMessage());
        }
    }

    private Response getEpic(int id) {
        Epic epic = taskManager.getEpicById(id);

        if (epic == null) {
            return new Response(404, String.format("Эпик с id = %d не найден.", id));
        } else {
            return new Response(200, gson.toJson(epic));
        }
    }

    private Response deleteEpic(int id) {
        Epic epic = taskManager.getEpic(id);

        if (epic == null) {
            return new Response(400, "Эпик, который вы хотите удалить не существует.");
        } else {
            taskManager.removeEpicById(id);
            return new Response(200, String.format("Эпик с id = %d удален.", id));
        }
    }

    private Response deleteAllEpics() {
        taskManager.removeAllEpics();
        return new Response(200, "Все задачи удалены.");
    }
}
