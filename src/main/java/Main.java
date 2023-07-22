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

//        Тест пересечения при создании таски/сабтаски
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



//        Тест рассчета времени Эпика
//        Epic epic1 = new Epic("TitleEpic1", "DescriptionEpic1");
//        taskManager.createEpic(epic1);
//        System.out.println(epic1);
//
//        Subtask subtask1 = new Subtask("TitleSubtask1",
//                "DescriptionSubtask1",
//                1, Instant.now(),
//                40);
//        taskManager.createSubtask(subtask1, 1);
//        System.out.println(epic1);
//
//        Subtask subtask2 = new Subtask("TitleSubtask1",
//                "DescriptionSubtask1",
//                1, Instant.now().plus(50, MINUTES),
//                40);
//        taskManager.createSubtask(subtask2, 1);
//        System.out.println(epic1);
//
//        taskManager.removeSubtaskById(2);
//        System.out.println(epic1);

//        Тест пересечения при обновлении таски/сабтаски
        Task task1 = new Task("TitleTask1",
                "DescriptionTask1",
                Instant.now(),
                40);
        Task task2 = new Task("TitleTask2",
                "DescriptionTask2",
                Instant.now().plus(50, MINUTES),
                40);
        Task task3 = new Task("TitleTask2",
                "DescriptionTask2",
                Instant.now().plus(10, MINUTES),
                40);

        taskManager.createTask(task1);
        System.out.println(taskManager.getTaskById(1));
        taskManager.createTask(task2);
        System.out.println(taskManager.getTaskById(2));
        taskManager.updateTask(task2, task3);
        System.out.println(taskManager.getTaskById(3));
    }
}
