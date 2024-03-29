package http.server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.server.adapter.InstantAdapter;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.List;

public class PrioritizedTasksHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedTasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(Instant.class, new InstantAdapter());
        this.gson = gsonBuilder.create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final int code;
        final String method = exchange.getRequestMethod();
        final String path = exchange.getRequestURI().getPath();
        final String response;

        if (method.equals("GET") && exchange.getRequestURI().getPath().equals("/tasks/")) {
            code = 200;
            List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
            response = gson.toJson(prioritizedTasks);
        } else {
            code = 400;
            response = String.format("Метод " + method + " не доступен для данного эндпоинта");
        }

        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/json");

        exchange.sendResponseHeaders(code, 0);
        if (response != null) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
