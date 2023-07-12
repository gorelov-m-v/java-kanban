import manager.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.constant.TaskStatus;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic1 = new Epic("Title1", "Description1");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Test1", "Test1", epic1.getId(), TaskStatus.DONE);
        manager.createSubtask(subtask1, epic1.getId());

        System.out.println(manager.getSubtaskById(2));
        Subtask newSubtaskData = new Subtask("NewTest1", "NewTest1", epic1.getId(), TaskStatus.DONE);
        manager.updateSubtask(2, newSubtaskData);
        System.out.println(manager.getSubtaskById(2));
    }
}
