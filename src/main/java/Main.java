
import manager.FileBackedTasksManager;
import manager.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.constants.TaskStatus;

import java.io.File;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        final Path PATH = Path.of("src/main/resources/test.csv");
        File file = new File(String.valueOf(PATH));
        FileBackedTasksManager fb = new FileBackedTasksManager(file);

        System.out.println("Поехали!");


        Epic epic = new Epic("Title1", "Description1");

        Task task1 = new Task("Test1", "Test1");
        Epic epic2 = new Epic("Title1", "Description1");

//        Epic epic2 = new Epic("Title1", "Description1");
//        Subtask subtask3 = new Subtask("Title", "Title", epic, TaskStatuses.NEW);
//        Subtask subtask4 = new Subtask("Title1", "Title1", epic, TaskStatuses.NEW);
//        Subtask subtask5 = new Subtask("Title2", "Title2", epic, TaskStatuses.NEW);


//        manager.createEpic(epic);
//        System.out.println(manager.getAllEpics());
        fb.createEpic(epic);
        Subtask subtask = new Subtask("Title", "Title", epic.getId(), TaskStatus.NEW);
        Subtask subtask1 = new Subtask("Title1", "Title1", epic.getId(), TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Title2", "Title2", epic.getId(), TaskStatus.NEW);
        fb.createSubtask(subtask, 1);
        fb.createSubtask(subtask2, 1);
        fb.getEpicById(1);
        fb.getSubtaskById(2);
        fb.getSubtaskById(3);
//        manager.createSubtask(subtask, epic.getId());
//        manager.createSubtask(subtask1, epic.getId());
//        manager.createSubtask(subtask2, epic.getId());

//        manager.createTask(task1);
//        manager.createEpic(epic2);
//
//        manager.getEpicById(1);
//        manager.getSubtaskById(2);
//        manager.getEpicById(1);
//        manager.getTaskById(5);
//        manager.getEpicById(6);
//        System.out.println(manager.getEpicBySubtaskId(2).getId());


//        manager.getSubtaskById(22);
//        manager.getTaskById(7);
//        manager.getSubtaskById(4);
//        manager.getEpicById(6);


//        manager.removeSubtaskById(22);
//        manager.removeTaskById(51);


//        fb.createTask(task1);



        System.out.println(fb.taskToSCV(subtask));
//        System.out.println("---------------------------------------------");
//        System.out.println(subtask);
//        System.out.println(fb.taskFromCSV(fb.taskToSCV(subtask)));



//        System.out.println(fb.historyToCsv().toString());
//        System.out.println(fb.historyFromCSV(fb.historyToCsv().toString()));
//
//        System.out.println(manager.historyManager.getHistory());
    }
}
