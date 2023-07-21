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

//        Task task1 = new Task("TitleTask1",
//                "DescriptionTask1",
//                Instant.now(),
//                40);
//        Task task2 = new Task("TitleTask2",
//                "DescriptionTask2",
//                Instant.now().plus(10, MINUTES),
//                40);
//
//        taskManager.createTask(task1);
//        System.out.println(taskManager.getTaskById(1));
//        taskManager.createTask(task2);
//        System.out.println(taskManager.getTaskById(2));

        Epic epic1 = new Epic("TitleEpic1", "DescriptionEpic1");

        Subtask subtask1 = new Subtask("TitleSubtask1",
                "DescriptionSubtask1",
                1, Instant.now(),
                40);

        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1, 1);
        System.out.println(epic1);
    }
}
