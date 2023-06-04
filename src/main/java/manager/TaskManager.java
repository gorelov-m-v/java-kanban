package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.constants.TaskStatuses;
import java.util.List;
import java.util.stream.Collectors;

public interface TaskManager {

    public List<Task> getAllTasks();
    public List<Epic> getAllEpics();
    public List<Subtask> getAllSubtasks();

    public void removeAllTasks();
    public void removeAllEpics();
    public void removeAllSubtasks();

    public Task getTaskById(int id);
    public Epic getEpicById(int id);
    public Subtask getSubtaskById(int id);

    public List<Subtask> getAllSubtasksFromEpic(int epicId);

    public void createTask(Task task);
    public void createEpic(Epic epic);
    public void createSubtask(Subtask subtask, Epic epic);

    public void updateTask(Task task, String title, String description, TaskStatuses status);
    public void updateEpic(Epic epic, String title, String description);
    public void updateSubtask(int subtaskId, Subtask newSubtaskData);

    public void removeTaskById(int id);
    public void removeEpicById(int id);
    public void removeSubtaskById(int id);
}
