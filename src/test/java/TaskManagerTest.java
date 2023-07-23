import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.exception.ManagerValidateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    T manager;
    abstract T createManager();

    @BeforeEach
    void getManager() {
        manager = createManager();
    }

    @Test
    public void shouldCreateTask() {
        Task task = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);

        manager.createTask(task);

        assertEquals(task, manager.getAllTasks().get(0));
    }

    @Test
    public void shouldCreateEpic() {
        Epic epic = new Epic("TestTaskTitle", "TestTaskDescription");

        manager.createEpic(epic);

        assertEquals(epic, manager.getAllEpics().get(0));
    }

    @Test
    public void shouldCreateSubtask() {
        Epic epic = new Epic("TestTaskTitle", "TestTaskDescription");
        manager.createEpic(epic);

        Subtask subtask = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1, Instant.now(), 30);
        manager.createSubtask(subtask, 1);

        assertEquals(subtask, manager.getAllSubtasks().get(0));
    }

    @Test
    public void shouldReturnTaskById() {
        Task task = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        manager.createTask(task);

        assertEquals(task, manager.getTaskById(1));
    }

    @Test
    public void shouldReturnNullTaskByWrongId() {
        Task task = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        manager.createTask(task);

        assertNull(manager.getTaskById(10));
    }

    @Test
    public void shouldReturnEpicById() {
        Epic epic = new Epic("TestTaskTitle", "TestTaskDescription");
        manager.createEpic(epic);

        assertEquals(epic, manager.getEpicById(1));
    }

    @Test
    public void shouldReturnNullEpicByWrongId() {
        Epic epic = new Epic("TestTaskTitle", "TestTaskDescription");
        manager.createEpic(epic);

        assertNull(manager.getEpicById(10));
    }

    @Test
    public void shouldReturnSubtaskById() {
        Epic epic = new Epic("TestTaskTitle", "TestTaskDescription");
        manager.createEpic(epic);

        Subtask subtask = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1, Instant.now(), 30);
        manager.createSubtask(subtask, 1);

        assertEquals(subtask, manager.getSubtaskById(2));
    }

    @Test
    public void shouldReturnNullSubtaskById() {
        Epic epic = new Epic("TestTaskTitle", "TestTaskDescription");
        manager.createEpic(epic);

        Subtask subtask = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1, Instant.now(), 30);
        manager.createSubtask(subtask, 1);

        assertNull(manager.getSubtaskById(20));
    }

    @Test
    public void shouldUpdateTask() {
        Task task = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        manager.createTask(task);

        Task newTask = new Task("NewTestTaskTitle", "NewTestTaskDescription", Instant.now(), 30);
        manager.updateTask(1, newTask);

        assertEquals(newTask, manager.getTaskById(1));
    }

    @Test
    public void shouldDoNothingWhenUpdateWrongTask() {
        Task task = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        manager.createTask(task);

        Task newTask = new Task("NewTestTaskTitle", "NewTestTaskDescription", Instant.now(), 30);
        manager.updateTask(10, newTask);

        assertEquals(task, manager.getTaskById(1));
    }

    @Test
    public void shouldDoNothingWhenUpdateNullTask() {
        Task task = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        manager.createTask(task);

        Task newTask = new Task("NewTestTaskTitle", "NewTestTaskDescription", Instant.now(), 30);

        manager.updateTask(null, newTask);

        assertEquals(task, manager.getTaskById(1));
    }

    @Test
    public void shouldUpdateSubtask() {
        Epic epic = new Epic("TestTaskTitle", "TestTaskDescription");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1, Instant.now(), 30);
        manager.createSubtask(subtask, 1);
        Subtask newSubtask = new Subtask(
                "NewTestSubtaskTitle", "NewTestSubtaskDescription", 1, Instant.now(), 40);


        manager.updateSubtask(2, newSubtask);

        assertEquals(newSubtask, manager.getSubtaskById(2));
    }

    @Test
    public void shouldThrowExceptionWhenUpdateWrongSubtask() {
        Epic epic = new Epic("TestTaskTitle", "TestTaskDescription");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1, Instant.now(), 30);
        manager.createSubtask(subtask, 1);
        Subtask newSubtask = new Subtask(
                "NewTestSubtaskTitle", "NewTestSubtaskDescription", 1, Instant.now(), 40);

        manager.updateSubtask(10, newSubtask);

        assertEquals(subtask, manager.getSubtaskById(2));
    }

    @Test
    public void shouldDoNothingWhenUpdateNullSubtask() {
        Epic epic = new Epic("TestTaskTitle", "TestTaskDescription");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1, Instant.now(), 30);
        manager.createSubtask(subtask, 1);
        Subtask newSubtask = new Subtask(
                "NewTestSubtaskTitle", "NewTestSubtaskDescription", 1, Instant.now(), 40);


        manager.updateSubtask(null, newSubtask);

        assertEquals(subtask, manager.getSubtaskById(2));
    }

    @Test
    public void shouldUpdateEpic() {
        Epic epic = new Epic("TestTaskTitle", "TestTaskDescription");
        manager.createEpic(epic);
        Epic newEpic = new Epic("NewTestTaskTitle", "NewTestTaskDescription");

        manager.updateEpic(1, newEpic);

        assertEquals(newEpic, manager.getEpicById(1));
    }

    @Test
    public void shouldDoNothingWhenUpdateWrongEpic() {
        Epic epic = new Epic("TestTaskTitle", "TestTaskDescription");
        manager.createEpic(epic);
        Epic newEpic = new Epic("NewTestTaskTitle", "NewTestTaskDescription");

        manager.updateEpic(10, newEpic);

        assertEquals(epic, manager.getEpicById(1));
    }

    @Test
    public void shouldDoNothingWhenUpdateNullEpic() {
        Epic epic = new Epic("TestTaskTitle", "TestTaskDescription");
        manager.createEpic(epic);
        Epic newEpic = new Epic("NewTestTaskTitle", "NewTestTaskDescription");

        manager.updateEpic(null, newEpic);

        assertEquals(epic, manager.getEpicById(1));
    }

    @Test
    public void shouldRemoveTaskById() {
        Task task = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        manager.createTask(task);

        manager.removeTaskById(1);

        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    public void shouldRemoveEpicBiId() {
        Epic epic = new Epic("TestTaskTitle", "TestTaskDescription");
        manager.createEpic(epic);

        manager.removeEpicById(1);

        assertEquals(0, manager.getAllEpics().size());
    }

    @Test
    public void shouldRemoveSubtaskById() {
        Epic epic = new Epic("TestTaskTitle", "TestTaskDescription");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1, Instant.now(), 30);
        manager.createSubtask(subtask, 1);

        manager.removeSubtaskById(2);

        assertEquals(0, manager.getAllSubtasks().size());
    }

    @Test
    public void shouldRemoveAllTasks() {
        Task task = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        manager.createTask(task);

        manager.removeAllTasks();

        assertEquals(0, manager.getAllEpics().size());
    }

    @Test
    public void shouldRemoveAllEpics() {
        Epic epic = new Epic("TestTaskTitle", "TestTaskDescription");
        manager.createEpic(epic);

        manager.removeAllEpics();

        assertEquals(0, manager.getAllEpics().size());
    }

    @Test
    public void shouldRemoveAllSubtasks() {
        Epic epic = new Epic("TestTaskTitle", "TestTaskDescription");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1, Instant.now(), 30);
        manager.createSubtask(subtask, 1);

        manager.removeAllSubtasks();

        assertEquals(0, manager.getAllSubtasks().size());
    }
}
