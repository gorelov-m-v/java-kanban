import manager.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.constant.TaskStatus;
import model.exception.ManagerIntersectionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import java.time.Instant;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{
    @Override
    InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }

    @Test
    public void shouldThrowWhenGetFullCreateIntersection() {
        Task task1 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        manager.createTask(task1);
        Task task2 = new Task("TestTaskTitle", "TestTaskDescription",
                Instant.now().plus(10, MINUTES), 20);

        final ManagerIntersectionException exception = assertThrows(
                ManagerIntersectionException.class,
                new Executable() {
                    @Override
                    public void execute(){
                        manager.checkCreateIntersection(task2);
                    }
                });

        assertEquals(String.format(
                "Задача, которую вы хотите создать/изменить, пересекается с задачей с id = %d.",
                task1.getId()), exception.getMessage());
    }

    @Test
    public void shouldThrowWhenGetLeftCreateIntersection() {
        Task task1 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        manager.createTask(task1);
        Task task2 = new Task("TestTaskTitle", "TestTaskDescription",
                Instant.now().plus(10, MINUTES), 70);

        final ManagerIntersectionException exception = assertThrows(
                ManagerIntersectionException.class,
                new Executable() {
                    @Override
                    public void execute(){
                        manager.checkCreateIntersection(task2);
                    }
                });

        assertEquals(String.format(
                "Задача, которую вы хотите создать/изменить, пересекается с задачей с id = %d.",
                task1.getId()), exception.getMessage());
    }

    @Test
    public void shouldThrowWhenGetRightCreateIntersection() {
        Task task1 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        manager.createTask(task1);
        Task task2 = new Task("TestTaskTitle", "TestTaskDescription",
                Instant.now().minus(10, MINUTES), 20);
        manager.updateTaskEndTime(task2);

        final ManagerIntersectionException exception = assertThrows(
                ManagerIntersectionException.class,
                new Executable() {
                    @Override
                    public void execute(){
                        manager.checkCreateIntersection(task2);
                    }
                });

        assertEquals(String.format(
                "Задача, которую вы хотите создать/изменить, пересекается с задачей с id = %d.",
                task1.getId()), exception.getMessage());
    }

    @Test
    public void shouldNotThrowWhenDonTGetCreateIntersection() {
        Task task1 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        manager.createTask(task1);
        Task task2 = new Task("TestTaskTitle", "TestTaskDescription",
                Instant.now().minus(10, MINUTES), 170);
        manager.updateTaskEndTime(task2);

        assertDoesNotThrow(
                new Executable() {
                    @Override
                    public void execute() {
                        manager.checkCreateIntersection(task2);
                    }
                });
    }

    @Test
    public void shouldThrowWhenGetFullUpdateIntersection() {
        Task task1 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        manager.createTask(task1);

        Task task3 = new Task("TestTaskTitle", "TestTaskDescription",
                Instant.now().plus(10, MINUTES), 20);
        task3.setId(2);

        final ManagerIntersectionException exception = assertThrows(
                ManagerIntersectionException.class,
                new Executable() {
                    @Override
                    public void execute(){
                        manager.checkUpdateIntersection(task3);
                    }
                });

        assertEquals(String.format(
                "Задача, которую вы хотите создать/изменить, пересекается с задачей с id = %d.",
                task1.getId()), exception.getMessage());
    }

    @Test
    public void shouldThrowWhenGetLeftUpdateIntersection() {
        Task task1 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        manager.createTask(task1);

        Task task3 = new Task("TestTaskTitle", "TestTaskDescription",
                Instant.now().plus(10, MINUTES), 170);
        task3.setId(2);

        final ManagerIntersectionException exception = assertThrows(
                ManagerIntersectionException.class,
                new Executable() {
                    @Override
                    public void execute(){
                        manager.checkUpdateIntersection(task3);
                    }
                });

        assertEquals(String.format(
                "Задача, которую вы хотите создать/изменить, пересекается с задачей с id = %d.",
                task1.getId()), exception.getMessage());
    }

    @Test
    public void shouldThrowWhenGetRightUpdateIntersection() {
        Task task1 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        manager.createTask(task1);

        Task task3 = new Task("TestTaskTitle", "TestTaskDescription",
                Instant.now().minus(10, MINUTES), 20);
        task3.setId(2);
        manager.updateTaskEndTime(task3);

        final ManagerIntersectionException exception = assertThrows(
                ManagerIntersectionException.class,
                new Executable() {
                    @Override
                    public void execute(){
                        manager.checkUpdateIntersection(task3);
                    }
                });

        assertEquals(String.format(
                "Задача, которую вы хотите создать/изменить, пересекается с задачей с id = %d.",
                task1.getId()), exception.getMessage());
    }

    @Test
    public void shouldNotThrowWhenDonTGetUpdateIntersection() {
        Task task1 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        manager.createTask(task1);

        Task task3 = new Task("TestTaskTitle", "TestTaskDescription",
                Instant.now().minus(10, MINUTES), 100);
        manager.createTask(task3);

        assertDoesNotThrow(
                new Executable() {
                    @Override
                    public void execute() {
                        manager.checkUpdateIntersection(task3);
                    }
                });
    }

    @Test
    public void shouldNotThrowWhenUpdateThemself() {
        Task task1 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now(), 40);
        manager.createTask(task1);

        Task task3 = new Task("TestTaskTitle", "TestTaskDescription",
                Instant.now().plus(10, MINUTES), 20);
        task3.setId(1);

        assertDoesNotThrow(
                new Executable() {
                    @Override
                    public void execute() {
                        manager.checkUpdateIntersection(task3);
                    }
                });
    }

    @Test
    public void shouldReturnNewEpicStatus() {
        Epic epic = new Epic("TestTaskTitle", "TestTaskDescription");
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1, Instant.now(), 30);
        manager.createSubtask(subtask1, 1);
        Subtask subtask2 = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1,
                Instant.now().plus(50, MINUTES), 30);
        manager.createSubtask(subtask2, 1);

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void shouldReturnDoneEpicStatus() {
        Epic epic = new Epic("TestTaskTitle", "TestTaskDescription");
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1,
                Instant.now(), 30);
        manager.createSubtask(subtask1, 1);
        Subtask newSubtask1 = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1,
                Instant.now(), 30, TaskStatus.DONE);
        manager.updateSubtask(2, newSubtask1);

        Subtask subtask2 = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1,
                Instant.now().plus(50, MINUTES), 30);
        manager.createSubtask(subtask2, 1);
        Subtask newSubtask2 = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1,
                Instant.now().plus(50, MINUTES), 30, TaskStatus.DONE);
        manager.updateSubtask(3, newSubtask2);

        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void shouldReturnInProgressEpicStatus() {
        Epic epic = new Epic("TestTaskTitle", "TestTaskDescription");
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1,
                Instant.now(), 30);
        manager.createSubtask(subtask1, 1);
        Subtask newSubtask1 = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1,
                Instant.now(), 30, TaskStatus.DONE);
        manager.updateSubtask(2, newSubtask1);

        Subtask subtask2 = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1,
                Instant.now().plus(50, MINUTES), 30);
        manager.createSubtask(subtask2, 1);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldUpdateEpicTimes() {
        Epic epic = new Epic("TestTaskTitle", "TestTaskDescription");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1,
                Instant.now(), 30);
        manager.createSubtask(subtask1, 1);

        assertEquals(subtask1.getStartTime() ,epic.getStartTime());
        assertEquals(subtask1.getDuration() ,epic.getDuration());
        assertEquals(subtask1.getEndTime() ,epic.getEndTime());
    }

    @Test
    public void shouldUpdateEpicTimesWithMultipleSubtasks() {
        Epic epic = new Epic("TestTaskTitle", "TestTaskDescription");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1,
                Instant.now(), 30);
        manager.createSubtask(subtask1, 1);

        Subtask subtask2 = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1,
                Instant.now().plus(40, MINUTES), 30);
        manager.createSubtask(subtask2, 1);

        assertEquals(subtask1.getStartTime() ,epic.getStartTime());
        assertEquals(60 ,epic.getDuration());
        assertEquals(subtask2.getEndTime() ,epic.getEndTime());
    }
}
