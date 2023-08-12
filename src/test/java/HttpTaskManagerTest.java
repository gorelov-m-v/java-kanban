import http.server.KVServer;
import manager.HttpTaskManager;
import manager.Managers;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import static model.constant.Keys.*;
import static org.assertj.core.api.Assertions.*;

public class HttpTaskManagerTest {

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
    @DisplayName("Сверяем сохраненные и загруженные Задачи.")
    void checkSavedAndLoadedTasks() {
        Task task1 = new Task("EXE1", "payment1",
                Instant.parse("2018-11-30T18:35:24.00Z"), 40);
        Task task2 = new Task("EXE2", "payment2",
                Instant.parse("2018-12-30T18:35:24.00Z"), 40);

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        List<Task> before = taskManager.getAllTasks();

        taskManager.tasks.clear();
        taskManager.loadByKey(TASKS);

        List<Task> after = taskManager.getAllTasks();

        assertThat(after).isEqualTo(before);
    }

    @Test
    @DisplayName("Сверяем сохраненные и загруженные Эпики.")
    void checkSavedAndLoadedEpics() {
        Epic epic1 = new Epic("EXE1", "payment1");
        Epic epic2 = new Epic("EXE2", "payment2");

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        List<Task> before = taskManager.getAllEpics();

        taskManager.epics.clear();
        taskManager.loadByKey(EPICS);

        List<Task> after = taskManager.getAllEpics();

        assertThat(after).isEqualTo(before);
    }

    @Test
    @DisplayName("Сверяем сохраненные и загруженные Сабтаски.")
    void checkSavedAndLoadedSubtasks() {
        Epic epic1 = new Epic("EXE1", "payment1");
        Subtask subtask1 = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1,
                Instant.parse("2018-11-30T18:35:24.00Z"), 30);
        Subtask subtask2 = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1,
                Instant.parse("2018-12-30T18:35:24.00Z"), 30);

        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1, 1);
        taskManager.createSubtask(subtask2, 1);

        List<Subtask> before = taskManager.getAllSubtasks();

        taskManager.subtasks.clear();
        taskManager.loadByKey(SUBTASKS);

        List<Subtask> after = taskManager.getAllSubtasks();

        assertThat(after).isEqualTo(before);
    }

    @Test
    @DisplayName("Сверяем сохраненную и загруженную истории.")
    void checkSavedAndLoadedTasks2() {
        Task task1 = new Task("EXE1", "payment1",
                Instant.parse("2018-11-30T18:35:24.00Z"), 40);
        Task task2 = new Task("EXE2", "payment2",
                Instant.parse("2018-12-30T18:35:24.00Z"), 40);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);

        List<Task> before = taskManager.getHistory();
        taskManager.loadByKey(HISTORY);
        List<Task> after = taskManager.getHistory();

        assertThat(after).isEqualTo(before);
    }

    @AfterEach
    void stopServer() {
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();
        kvServer.stop();
    }
}
