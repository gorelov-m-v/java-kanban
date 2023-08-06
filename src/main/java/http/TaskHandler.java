package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Task;
import model.exception.ManagerIntersectionException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
        final int code;
        final String method = exchange.getRequestMethod();
        final String path = exchange.getRequestURI().getPath();
        String response;

        if (method.equals("POST")) {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

            try {
                Task newTaskData = gson.fromJson(body, Task.class);
                taskManager.createTask(newTaskData);

                OptionalInt newTaskId = taskManager.getAllTasks().stream()
                        .map(Task::getId)
                        .mapToInt(id -> id)
                        .max();
                Task newTask = taskManager.getTask(newTaskId.getAsInt());

                Headers headers = exchange.getResponseHeaders();
                headers.set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, 0);

                response = gson.toJson(newTask);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                } finally {
                    exchange.close();
                }
            } catch (ManagerIntersectionException e) {
                response = e.getMessage();

                Headers headers = exchange.getResponseHeaders();
                headers.set("Content-Type", "text/plain");
                exchange.sendResponseHeaders(400, 0);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                } finally {
                    exchange.close();
                }
            }
        }
    }
}
