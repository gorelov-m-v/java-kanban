import model.Epic;
import model.Manager;
import model.Subtask;
import model.Task;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");

        Epic epic1 = new Epic("Test", "Test1");
        Subtask subtask1 = new Subtask("Test1", "Test1", epic1, "NEW");
        Subtask subtask2 = new Subtask("Test2", "Test2", epic1, "NEW");
        Subtask subtask3 = new Subtask("Test3", "Test3", epic1, "NEW");

        Manager manager = new Manager();

        manager.createEpic(epic1); // id = 1

//        System.out.println(manager.getAllEpics().get(0).getSubtasks());

        manager.createSubtask(subtask1, epic1); // id = 2
        manager.createSubtask(subtask2, epic1); // id = 3
        manager.createSubtask(subtask3, epic1); // id = 4

//        System.out.println(manager.getAllEpics().get(0).getSubtasks());
//
//        System.out.println(manager.getSubtaskById(3)); // Работает
//
//        System.out.println(manager.getEpicById(1)); // Работает.
//
//        System.out.println(manager.getEpicBySubtaskId(2));

//        manager.removeAllSubtasks(manager.getEpicById(1));
//        System.out.println(manager.getAllEpics().get(0).getSubtasks()); // Работает.

        System.out.println(manager.getIndexBySubtaskId(3));

        System.out.println(manager.getAllEpics().get(0).getSubtasks());
        manager.updateSubtask(2,new Subtask("Test2", "Test2", epic1, "DONE"));
        manager.updateSubtask(3,new Subtask("Test2", "Test2", epic1, "DONE"));
        manager.updateSubtask(4,new Subtask("Test2", "Test2", epic1, "DONE"));
        System.out.println(manager.getAllEpics().get(0).getSubtasks());

        System.out.println(manager.getAllEpics().get(0));

    }





}
