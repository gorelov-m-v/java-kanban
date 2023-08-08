package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.List;

public class HistoryHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public HistoryHandler(TaskManager taskManager) {
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

        if (method.equals("GET")) {
            response = getHistory();
        } else {
            response = new Response(405, "Метод не поддерживается. Доступны: GET");
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

    private Response getHistory() {
        List<Task> history = taskManager.getHistory();
        return new Response(200, gson.toJson(history));
    }
}
