import manager.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.constant.TaskStatus;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.time.temporal.ChronoUnit.MINUTES;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("TestTaskTitle", "TestTaskDescription", null, 0);
        Task task2 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now().plus(100, MINUTES), 40);
        Task task3 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now().plus(150, MINUTES), 40);
        Task task4 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now().plus(200, MINUTES), 40);
        Task task5 = new Task("NewTestTaskTitle", "NewTestTaskDescription", Instant.now().plus(700, MINUTES), 40);
        Epic epic1 = new Epic("TestTaskTitle", "TestTaskDescription");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(
                "TestSubtaskTitle", "TestSubtaskDescription", 1,
                Instant.now().plus(2250, MINUTES), 30);
        taskManager.createSubtask(subtask1, 1);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);



        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getPrioritizedTasks());
    }
}
