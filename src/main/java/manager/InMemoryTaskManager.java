package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.constant.TaskStatus;
import model.constant.TaskType;
import model.exception.ManagerCreateIntersectionException;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {
    public HistoryManager historyManager = Managers.getDefaultHistoryManager();

    public final Map<Integer, Task> tasks = new HashMap<>();
    public final Map<Integer, Epic> epics = new HashMap<>();
    public final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    private int i = 0;


    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }
    @Override
    public List<Task> getAllEpics() {
        return new ArrayList<>(epics.values());
    }
    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
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
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        Optional<Task> optionalTask = Optional.ofNullable(task);

        optionalTask.ifPresent(t -> historyManager.add(t));

        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        Optional<Task> optionalTask = Optional.ofNullable(epic);

        optionalTask.ifPresent(s -> historyManager.add(s));

        return epic;
    }

    @Override
    public Task getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        Optional<Task> optionalTask = Optional.ofNullable(subtasks.get(id));

        optionalTask.ifPresent(s -> historyManager.add(s));

        return subtask;
    }

    @Override
    public List<Integer> getAllSubtasksFromEpic(int epicId) {
        return epics.values().stream()
                .filter(epic -> epic.getId() == epicId)
                .map(Epic::getSubtasks)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public void createTask(Task task) {
        try {
            checkIntersection(task);
            task.setId(getNewId());
            tasks.put(task.getId(), task);
        } catch (ManagerCreateIntersectionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(getNewId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask, int epicId) {
        try {
            checkIntersection(subtask);
            subtask.setId(getNewId());
            Epic epic = getEpic(epicId);
            epic.addSubtask(subtask.getId());

            subtasks.put(subtask.getId(), subtask);
            updateEpicTime(epic);
        } catch (ManagerCreateIntersectionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateTask(Task task, String title, String description, TaskStatus status) {
        Task tempTask = task;
        tempTask.setTitle(title);
        tempTask.setDescription(description);
        tempTask.setStatus(status);

        try {
            checkIntersection(tempTask);

            tasks.put(task.getId(), tempTask);
        } catch (ManagerCreateIntersectionException e) {
            System.out.println(e.getMessage());
        }

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
        try {
            checkIntersection(newSubtaskData);
            Optional<Epic> epicOptional = Optional.ofNullable(getEpicBySubtaskId(subtaskId));

            epicOptional.ifPresent(e -> {
                subtasks.remove(subtaskId);
                subtasks.put(subtaskId, newSubtaskData);

                Epic epic = getEpicBySubtaskId(subtaskId);

                updateEpicTime(epic);
                checkEpicStatus(epic);
            });
        } catch (ManagerCreateIntersectionException e) {
            System.out.println(e.getMessage());
        }


    }

    @Override
    public void removeTaskById(int id) {
        historyManager.removeById(id);

        tasks.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        getAllSubtasksFromEpic(id)
                .forEach(subtaskId -> historyManager.removeById(subtaskId));

        historyManager.removeById(id);

        epics.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        Optional<Task> optionalSubtask = Optional.ofNullable(getSubtaskById(id));

        optionalSubtask.ifPresent(s -> {
            Epic epic = getEpicBySubtaskId(id);

            epic.removeSubtask(id);
            updateEpicTime(epic);

            historyManager.removeById(id);
        });
    }

    @Override
    public Epic getEpicBySubtaskId(int subtaskId) {
        return epics.values().stream()
                .filter(epic -> epic.getSubtasks().contains(subtaskId))
                .findFirst()
                .orElse(null);
    }
    @Override
    public TaskType getType(Task task) {
        if (task.getClass() == Epic.class) {
            return TaskType.EPIC;
        } else if (task.getClass() == Task.class) {
            return TaskType.TASK;
        } else {
            return TaskType.SUBTASK;
        }
    }

    public int getNewId() {
        return ++i;
    }

    public void checkEpicStatus(Epic epic) {
        List<TaskStatus> subtaskStatuses = epic.getSubtasks()
                .stream()
                .map(this::getSubtaskById)
                .map(Task::getStatus)
                .collect(Collectors.toList());


        boolean statusNew = subtaskStatuses.stream().allMatch(s -> s.equals(TaskStatus.NEW));
        boolean statusDone = subtaskStatuses.stream().allMatch(s -> s.equals(TaskStatus.DONE));

        if (statusNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (statusDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private void updateEpicTime(Epic epic) {
        Instant startTime = epic.getSubtasks().stream()
                .map(this::getSubtaskById)
                .map(Task::getStartTime)
                .min(Instant::compareTo)
                .orElse(null);

        Instant endTime = epic.getSubtasks().stream()
                .map(this::getSubtaskById)
                .map(Task::getEndTime)
                .max(Instant::compareTo)
                .orElse(null);

        long duration = Duration.between(startTime, endTime).toMinutes();

        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        epic.setDuration(duration);
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public void checkIntersection(Task task) {
        Optional<Integer> intersectionTask = Stream.of(getAllSubtasks(), getAllTasks())
                .flatMap(List::stream)
                .filter(t ->
                        task.getStartTime().isAfter(t.getStartTime()) && task.getStartTime().isBefore(t.getEndTime())
                        ||
                        task.getEndTime().isAfter(t.getStartTime()) && task.getEndTime().isBefore(t.getEndTime()))
                .map(Task::getId)
                .findFirst();

        if (intersectionTask.isPresent()) {
            throw new ManagerCreateIntersectionException(String.format("Задача, которую вы хотите создать/изменить," +
                    " пересекается с задачей с id = %s.", intersectionTask.get()));
        }
    }

    public Epic getEpic(int id) {
        Epic epic = epics.get(id);

        return epic;
    }
}
