import manager.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.constants.TaskStatus;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        System.out.println("Поехали!");


        Epic epic = new Epic("Title1", "Description1");
        Subtask subtask = new Subtask("Title", "Title", epic, TaskStatus.NEW);
        Subtask subtask1 = new Subtask("Title1", "Title1", epic, TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Title2", "Title2", epic, TaskStatus.NEW);
        Task task1 = new Task("Test1", "Test1");
        Epic epic2 = new Epic("Title1", "Description1");

//        Epic epic2 = new Epic("Title1", "Description1");
//        Subtask subtask3 = new Subtask("Title", "Title", epic, TaskStatuses.NEW);
//        Subtask subtask4 = new Subtask("Title1", "Title1", epic, TaskStatuses.NEW);
//        Subtask subtask5 = new Subtask("Title2", "Title2", epic, TaskStatuses.NEW);


        manager.createEpic(epic);
        manager.createSubtask(subtask, epic);
        manager.createSubtask(subtask1, epic);
        manager.createSubtask(subtask2, epic);
        manager.createTask(task1);
        manager.createEpic(epic2);

        manager.getEpicById(1);
        manager.getSubtaskById(2);
        manager.getEpicById(1);
        manager.getTaskById(5);
        manager.getEpicById(6);
        manager.removeEpicById(1);
//        manager.getSubtaskById(22);
//        manager.getTaskById(7);
//        manager.getSubtaskById(4);
//        manager.getEpicById(6);


//        manager.removeSubtaskById(22);
//        manager.removeTaskById(51);



        System.out.println(manager.historyManager.getHistory());
    }
}
