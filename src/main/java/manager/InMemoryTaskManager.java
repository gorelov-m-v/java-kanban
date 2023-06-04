package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.constants.TaskStatuses;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    public HistoryManager historyManager = Managers.getDefaultHistoryManager();

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private int i = 0;


    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return epics.values()
                .stream()
                .map(Epic::getSubtasks)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
    }

    @Override
    public void removeAllSubtasks() {
        epics.values().stream()
                .map(Epic::getSubtasks)
                .forEach(List::clear);
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask =  epics.values()
                .stream()
                .map(Epic::getSubtasks)
                .flatMap(List::stream)
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElse(null);

        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public List<Subtask> getAllSubtasksFromEpic(int epicId) {
        return epics.values().stream()
                .filter(epic -> epic.getId() == epicId)
                .map(Epic::getSubtasks)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void createTask(Task task) {
        task.setId(getNewId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(getNewId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask, Epic epic) {
        subtask.setId(getNewId());

        epic.addSubtask(subtask);
    }

    @Override
    public void updateTask(Task task, String title, String description, TaskStatuses status) {
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic, String title, String description) {
        epic.setTitle(title);
        epic.setDescription(description);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(int subtaskId, Subtask newSubtaskData) {
        newSubtaskData.setId(subtaskId);

        int epicId = getEpicBySubtaskId(subtaskId).getId();

        epics.get(epicId).removeSubtask(subtaskId);
        epics.get(epicId).getSubtasks().add(newSubtaskData);

        checkEpicStatus(getEpicBySubtaskId(subtaskId));
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        epics.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        epics.values().stream()
                .filter(s -> s.getSubtasks().contains(getSubtaskById(id)))
                .forEach(e -> e.removeSubtask(id));
    }

    private Epic getEpicBySubtaskId(int subtaskId) {
        return epics.values().stream()
                .filter(epic -> epic.getSubtasks().contains(getSubtaskById(subtaskId)))
                .findFirst()
                .orElse(null);
    }

    private int getNewId() {
        return ++i;
    }

    private void checkEpicStatus(Epic epic) {
        List<TaskStatuses> subtaskStatuses = epic.getSubtasks()
                .stream()
                .map(Subtask::getStatus)
                .collect(Collectors.toList());


        boolean statusNew = subtaskStatuses.stream().allMatch(s -> s.equals(TaskStatuses.NEW));
        boolean statusDone = subtaskStatuses.stream().allMatch(s -> s.equals(TaskStatuses.DONE));

        if (statusNew) {
            epic.setStatus(TaskStatuses.NEW);
        } else if (statusDone) {
            epic.setStatus(TaskStatuses.DONE);
        } else {
            epic.setStatus(TaskStatuses.IN_PROGRESS);
        }
    }
}
