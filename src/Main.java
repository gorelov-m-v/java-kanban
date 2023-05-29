import manager.Manager;
import model.Epic;
import model.Subtask;
import model.constants.TaskStatuses;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        System.out.println("Поехали!");


        Epic epic = new Epic("Title1", "Description1");
        Subtask subtask = new Subtask("Title", "Title", epic, TaskStatuses.NEW);
        Subtask subtask1 = new Subtask("Title1", "Title1", epic, TaskStatuses.NEW);
        Subtask subtask2 = new Subtask("Title2", "Title2", epic, TaskStatuses.NEW);


        manager.createEpic(epic);
        manager.createSubtask(subtask, epic);
        manager.createSubtask(subtask1, epic);
        manager.createSubtask(subtask2, epic);

        System.out.println(manager.getAllEpics().get(0));

        System.out.println(manager.getAllSubtasks());
        System.out.println(manager.getSubtaskById(2));

    }
}
