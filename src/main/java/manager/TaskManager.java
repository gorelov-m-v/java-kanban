package manager;

import model.Epic;
import model.Subtask;
import model.Task;
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
    Epic getEpicById(int id);
    Subtask getSubtaskById(int id);

    List<Integer> getAllSubtasksFromEpic(int epicId);

    void createTask(Task task);
    void createEpic(Epic epic);
    void createSubtask(Subtask subtask, int epicId);

    void updateTask(Integer taskId, Task newTaskData);
    void updateEpic(Integer epicId, Epic newEpic);
    void updateSubtask(Integer subtaskId, Subtask newSubtask);

    void removeTaskById(int id);
    void removeEpicById(int id);
    void removeSubtaskById(int id);
    TaskType getType(Task task);
    Epic getEpicBySubtaskId(int subtaskId);
    List<Task> getPrioritizedTasks();
    List<Task> getHistory();
    Task getTask(int i);
}
