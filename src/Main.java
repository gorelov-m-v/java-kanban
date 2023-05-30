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

        Epic epic2 = new Epic("Title1", "Description1");
        Subtask subtask3 = new Subtask("Title", "Title", epic, TaskStatuses.NEW);
        Subtask subtask4 = new Subtask("Title1", "Title1", epic, TaskStatuses.NEW);
        Subtask subtask5 = new Subtask("Title2", "Title2", epic, TaskStatuses.NEW);


        manager.createEpic(epic);
        manager.createSubtask(subtask, epic);
        manager.createSubtask(subtask1, epic);
        manager.createSubtask(subtask2, epic);
        System.out.println(manager.getAllEpics().get(0));
        manager.updateSubtask(2, new Subtask("Title", "Title", epic, TaskStatuses.DONE));
        System.out.println(manager.getSubtaskById(2));
        System.out.println(manager.getEpicBySubtaskId(2));

//        manager.createEpic(epic2);
//
//        System.out.println(manager.getAllEpics().get(1));
//        manager.createSubtask(subtask3, epic2);
//        manager.createSubtask(subtask4, epic2);
//        manager.createSubtask(subtask5, epic2);
//        System.out.println(manager.getAllEpics().get(1));
//
//        manager.removeSubtaskById(3);
//        System.out.println(manager.getAllEpics().get(0));



    }
}
