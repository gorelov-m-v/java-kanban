package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.constant.TaskStatus;
import model.constant.TaskType;

import java.util.List;

public interface TaskManager {

    List<Task> getAllTasks();
    List<Task> getAllEpics();
    List<Subtask> getAllSubtasks();

    void removeAllTasks();
    void removeAllEpics();
    void removeAllSubtasks();

    Task getTaskById(int id);
    Task getEpicById(int id);
    Task getSubtaskById(int id);

    List<Integer> getAllSubtasksFromEpic(int epicId);

    void createTask(Task task);
    void createEpic(Epic epic);
    void createSubtask(Subtask subtask, int epicId);

    void updateTask(Task task, String title, String description, TaskStatus status);
    void updateEpic(Epic epic, String title, String description);
    void updateSubtask(int subtaskId, Subtask newSubtaskData);

    void removeTaskById(int id);
    void removeEpicById(int id);
    void removeSubtaskById(int id);
    TaskType getType(Task task);
    Epic getEpicBySubtaskId(int subtaskId);
}
