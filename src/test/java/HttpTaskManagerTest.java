import http.KVServer;
import manager.HttpTaskManager;
import manager.Managers;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static model.constant.Keys.TASKS;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager>{

    private String url = "http://localhost:10000";
    private KVServer kvServer;
    HttpTaskManager taskManager;

    @BeforeEach
    void initiateTaskManager() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            taskManager = Managers.getDefaultTaskManager();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void smokeTest() {

        // Создал 2 таски
        Task task1 = new Task("EXE1", "payment1", Instant.now(), 40);
        Task task2 = new Task("EXE2", "payment2",
                Instant.now().plus(50, ChronoUnit.MINUTES), 40);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        // Данные ушли на KV-сервер


        // Очистил мапу
        taskManager.tasks.clear();

        // Загрузил с сервера и посмотрел состояние менеджера.
        taskManager.loadByKey(TASKS);
        System.out.println(taskManager.getAllTasks());

    }

    @Override
    HttpTaskManager createManager() {
        return null;
    }
}
