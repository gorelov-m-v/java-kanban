import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryManagerTest {

    InMemoryHistoryManager manager;
    @BeforeEach
    public void getManager() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    public void shouldAddTaskToHistory() {
        Task task1 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        manager.add(task1);

        assertEquals(1, manager.getHistory().size());
    }

    @Test
    public void shouldRemoveFromHistory() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task1 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        taskManager.createTask(task1);

        manager.add(task1);

        manager.removeById(1);

        assertEquals(0, manager.getHistory().size());
    }

    @Test
    public void shouldReturnHistoryAsList() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task1 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        taskManager.createTask(task1);

        manager.add(task1);

        assertEquals(List.of(task1), manager.getHistory());
    }

}
