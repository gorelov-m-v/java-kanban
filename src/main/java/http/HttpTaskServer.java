package http;

import com.sun.net.httpserver.HttpServer;
import manager.FileBackedTasksManager;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;

public class HttpTaskServer {

    private static final int PORT = 8080;
    HttpServer httpServer;
    HistoryManager historyManager = Managers.getDefaultHistoryManager();
    private static final Path PATH = Path.of(
            "src" + File.separator +
                    "main" + File.separator +
                    "resources" + File.separator +
                    "test.csv");
    private File file = new File(String.valueOf(PATH));
    TaskManager taskManager = new FileBackedTasksManager(file);

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
    }
    public HttpTaskServer() throws IOException {
        this.httpServer = HttpServer.create();
        this.httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new PrioritizedTasksHandler(taskManager));
//        httpServer.createContext("/tasks/history", new HistoryHandler());
        httpServer.createContext("/tasks/task", new TaskHandler(taskManager));
//        httpServer.createContext("/tasks/subtask", new SubtaskHandler());
//        httpServer.createContext("/tasks/epic", new EpicHandler());
        httpServer.start();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        this.httpServer.stop(1);
    }
}
