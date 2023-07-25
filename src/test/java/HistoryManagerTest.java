import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryManagerTest {

    InMemoryHistoryManager historyManager;
    InMemoryTaskManager taskManager;

    @BeforeEach
    public void getManager() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void shouldAddTaskToHistory() {
        Task task1 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        historyManager.add(task1);

        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    public void shouldRemoveFromHistory() {
        Task task1 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        taskManager.createTask(task1);

        historyManager.add(task1);

        historyManager.removeById(1);

        assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    public void shouldReturnHistoryAsList() {
        Task task1 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        taskManager.createTask(task1);

        historyManager.add(task1);

        assertEquals(List.of(task1), historyManager.getHistory());
    }

    @Test
    public void shouldReturnEmptyListIfHistoryIsEmpty() {
        Task task1 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        taskManager.createTask(task1);

        assertEquals(List.of(), historyManager.getHistory());
    }

    @Test
    public void shouldReturnHistoryWithReGettingTusks() {
        Task task1 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        taskManager.createTask(task1);
        Epic epic1 = new Epic("TestTaskTitle", "TestTaskDescription");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1,
                Instant.now().plus(50, MINUTES), 30);
        taskManager.createSubtask(subtask1, 2);

        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1);

        historyManager.add(epic1);

        assertEquals(List.of(task1, subtask1, epic1), historyManager.getHistory());
    }

    @Test
    public void shouldReturnHistoryWithRemoveFromLeft() {
        Task task1 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        taskManager.createTask(task1);
        Epic epic1 = new Epic("TestTaskTitle", "TestTaskDescription");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1,
                Instant.now().plus(50, MINUTES), 30);
        taskManager.createSubtask(subtask1, 2);

        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1);

        historyManager.removeById(task1.getId());

        assertEquals(List.of(epic1, subtask1), historyManager.getHistory());
    }

    @Test
    public void shouldReturnHistoryWithRemoveFromLeftPath() {
        Task task1 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        taskManager.createTask(task1);
        Epic epic1 = new Epic("TestTaskTitle", "TestTaskDescription");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1,
                Instant.now().plus(50, MINUTES), 30);
        taskManager.createSubtask(subtask1, 2);

        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1);

        historyManager.removeById(subtask1.getId());

        assertEquals(List.of(task1, epic1), historyManager.getHistory());
    }

    @Test
    public void shouldReturnHistoryWithRemoveFromCenter() {
        Task task1 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        taskManager.createTask(task1);
        Epic epic1 = new Epic("TestTaskTitle", "TestTaskDescription");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1,
                Instant.now().plus(50, MINUTES), 30);
        taskManager.createSubtask(subtask1, 2);

        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1);

        historyManager.removeById(epic1.getId());

        assertEquals(List.of(task1, subtask1), historyManager.getHistory());
    }
}
