import manager.FileBackedTasksManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    @Override
    FileBackedTasksManager createManager() {
       final Path PATH = Path.of(
                "src" + File.separator +
                        "main" + File.separator +
                        "resources" + File.separator +
                        "test.csv");
       File file = new File(String.valueOf(PATH));
       return new FileBackedTasksManager(file);
    }

    @Test
    public void healthCheckFileManagerWithEmptyTaskList() {
        Epic epic1 = new Epic("TestTaskTitle", "TestTaskDescription");
        manager.createEpic(epic1);
        Subtask subtask1 = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1,
                Instant.now().plus(50, MINUTES), 30);
        manager.createSubtask(subtask1, 1);

        final Path PATH = Path.of(
                "src" + File.separator +
                        "main" + File.separator +
                        "resources" + File.separator +
                        "test.csv");
        File file = new File(String.valueOf(PATH));

        FileBackedTasksManager fileManagerFromCsv = FileBackedTasksManager.load(file);

        assertEquals(manager.getAllSubtasks(), fileManagerFromCsv.getAllSubtasks());
        assertEquals(manager.getAllEpics(), fileManagerFromCsv.getAllEpics());
        assertEquals(manager.getAllTasks(), fileManagerFromCsv.getAllTasks());
    }

    @Test
    public void healthCheckFileManagerWithTaskWithoutSubtasks() {
        Epic epic1 = new Epic("TestTaskTitle", "TestTaskDescription");
        manager.createEpic(epic1);
        Task task = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        manager.createTask(task);


        final Path PATH = Path.of(
                "src" + File.separator +
                        "main" + File.separator +
                        "resources" + File.separator +
                        "test.csv");
        File file = new File(String.valueOf(PATH));

        FileBackedTasksManager fileManagerFromCsv = FileBackedTasksManager.load(file);

        assertEquals(manager.getAllSubtasks(), fileManagerFromCsv.getAllSubtasks());
        assertEquals(manager.getAllEpics(), fileManagerFromCsv.getAllEpics());
        assertEquals(manager.getAllTasks(), fileManagerFromCsv.getAllTasks());
    }
}
