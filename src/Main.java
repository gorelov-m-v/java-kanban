import manager.Manager;
import model.Epic;
import model.Subtask;
import model.constants.TaskStatuses;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        System.out.println("Поехали!");


        Epic epic = new Epic("Title1", "Description1");
        Subtask subtask = new Subtask("Title", "Title1", epic, TaskStatuses.NEW);
        Subtask newSubtask = new Subtask("title2", "title2", epic, TaskStatuses.DONE);

        manager.createEpic(epic);
        manager.createSubtask(subtask, epic);
        System.out.println(manager.getAllEpics().get(0).getSubtasks().get(0));
        manager.updateSubtask(2, newSubtask);

        System.out.println(manager.getAllEpics().get(0).getSubtasks().get(0));
    }
}
