package manager;

import model.Epic;
import model.StartTimeComparator;
import model.Subtask;
import model.Task;
import model.constant.TaskStatus;
import model.constant.TaskType;
import model.exception.ManagerIntersectionException;
import model.exception.ManagerValidateException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static model.constant.TaskStatus.NEW;

public class InMemoryTaskManager implements TaskManager {
    public HistoryManager historyManager = Managers.getDefaultHistoryManager();

    public final Map<Integer, Task> tasks = new HashMap<>();
    public final Map<Integer, Epic> epics = new HashMap<>();
    public final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected Set<Task> prioritizedTasks = new TreeSet<>(new StartTimeComparator());
    private int i = 0;


    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Task> getAllEpics() {
        if (!epics.values().isEmpty()) {
            epics.values().stream().forEach(this::updateEpicTime);
            return new ArrayList<>(epics.values());
        } else {
            return new ArrayList<>();
        }
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
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        Optional<Task> optionalTask = Optional.ofNullable(subtasks.get(id));

        optionalTask.ifPresent(s -> historyManager.add(s));

        return subtask;
    }

    @Override
    public List<Integer> getAllSubtasksFromEpic(int epicId) {

        return getEpic(epicId).getSubtasks();
    }

    @Override
    public void createTask(Task task) throws ManagerIntersectionException {
        updateTaskEndTime(task);
        checkCreateIntersection(task);
        task.setId(getNewId());
        task.setStatus(NEW);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(getNewId());
        epic.setStatus(NEW);
        epic.setSubtasks(new ArrayList<>());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask, int epicId) {
        try {
            if (epics.keySet().contains(epicId)) {
                try {
                    updateSubTaskEndTime(subtask);
                    checkCreateIntersection(subtask);
                    subtask.setId(getNewId());
                    subtask.setStatus(NEW);
                    subtask.setEpicId(epicId);
                    Epic epic = getEpic(epicId);
                    epic.addSubtask(subtask.getId());

                    subtasks.put(subtask.getId(), subtask);
                    updateEpicTime(epic);
                    checkEpicStatus(epic);
                    prioritizedTasks.add(subtask);
                } catch (ManagerIntersectionException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                throw new ManagerValidateException(String.format("Epic c id = %d не существует", epicId));
            }
        } catch (ManagerValidateException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateTask(Integer taskId, Task newTaskData) {
        if (taskId == null) {
            throw new ManagerValidateException("id не может быть null");
        } else if (!tasks.containsKey(taskId)) {
            throw new ManagerValidateException(String.format("Задачи с id = %d, не существует", taskId));
        }
        updateTaskEndTime(newTaskData);
        newTaskData.setId(taskId);
        checkUpdateIntersection(newTaskData);
        tasks.put(taskId, newTaskData);
    }

    @Override
    public void updateEpic(Integer epicId, Epic newEpic) {
        if (epicId == null) {
            throw new ManagerValidateException("id не может быть null");
        } else if (!epics.containsKey(epicId)) {
            throw new ManagerValidateException(String.format("Эпика с id = %d, не существует", epicId));
        }
        newEpic.setId(epicId);
        newEpic.setStatus(getEpic(epicId).getStatus());
        newEpic.setSubtasks(getEpic(epicId).getSubtasks());
        epics.put(epicId, newEpic);
    }

    @Override
    public void updateSubtask(Integer subtaskId, Subtask newSubtaskData) {
        if (subtaskId == null) {
            throw new ManagerValidateException("id не может быть null");
        } else if (!subtasks.containsKey(subtaskId)) {
            throw new ManagerValidateException(String.format("Подзадачи с id = %d, не существует", subtaskId));
        }
        updateSubTaskEndTime(newSubtaskData);
        newSubtaskData.setId(subtaskId);
        newSubtaskData.setEpicId(getSubtask(subtaskId).getEpicId());
        checkUpdateIntersection(newSubtaskData);
        Optional<Epic> epicOptional = Optional.ofNullable(getEpicBySubtaskId(subtaskId));

        epicOptional.ifPresent(e -> {
            subtasks.remove(subtaskId);
            subtasks.put(subtaskId, newSubtaskData);

            Epic epic = getEpicBySubtaskId(subtaskId);

            updateEpicTime(epic);
            checkEpicStatus(epic);
        });
    }

    @Override
    public void removeTaskById(int id) {
        historyManager.removeById(id);

        tasks.remove(id);
        if (tasks.containsValue(getTaskById(id))) {
            prioritizedTasks.remove(getTaskById(id));
        }
    }

    @Override
    public void removeEpicById(int id) {
        if (getAllSubtasksFromEpic(id) != null) {
            getAllSubtasksFromEpic(id)
                    .forEach(subtaskId -> historyManager.removeById(subtaskId));
        }

        historyManager.removeById(id);

        epics.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        Optional<Task> optionalSubtask = Optional.ofNullable(getSubtaskById(id));

        optionalSubtask.ifPresent(s -> {
            Epic epic = getEpicBySubtaskId(id);

            epic.removeSubtask(id);
            subtasks.remove(id);
            updateEpicTime(epic);
            if (subtasks.containsValue(getSubtaskById(id))) {
                prioritizedTasks.remove(getSubtask(id));
            }
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public int getNewId() {
        return ++i;
    }

    public void checkEpicStatus(Epic epic) {
        List<TaskStatus> subtaskStatuses = epic.getSubtasks()
                .stream()
                .map(this::getSubtask)
                .map(Subtask::getStatus)
                .collect(Collectors.toList());


        boolean statusNew = subtaskStatuses.stream().allMatch(s -> s.equals(NEW));
        boolean statusDone = subtaskStatuses.stream().allMatch(s -> s.equals(TaskStatus.DONE));

        if (statusNew) {
            epic.setStatus(NEW);
        } else if (statusDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public void updateEpicTime(Epic epic) {
        if (!epic.getSubtasks().isEmpty()) {
            Instant startTime = epic.getSubtasks().stream()
                    .map(this::getSubtask)
                    .map(Task::getStartTime)
                    .filter(Objects::nonNull)
                    .min(Instant::compareTo)
                    .orElse(null);

            Instant endTime = epic.getSubtasks().stream()
                    .map(this::getSubtask)
                    .map(Task::getEndTime)
                    .filter(Objects::nonNull)
                    .max(Instant::compareTo)
                    .orElse(null);


            long duration;
            if (startTime == null && endTime == null) {
                duration = 0;
            } else {
                duration = epic.getSubtasks().stream()
                        .map(this::getSubtask)
                        .map(Task::getDuration)
                        .mapToLong(Long::longValue)
                        .sum();
            }

            epic.setStartTime(startTime);
            epic.setEndTime(endTime);
            epic.setDuration(duration);
        }
    }

    public void updateTaskEndTime(Task task) {
        if (task.getStartTime() == null || task.getDuration() == 0) {
            return;
        }
        task.setEndTime(task.getStartTime().plusSeconds(task.getDuration() * 60));
    }

    public void updateSubTaskEndTime(Subtask subtask) {
        if (subtask.getStartTime() == null || subtask.getDuration() == 0) {
            return;
        }
        subtask.setEndTime(subtask.getStartTime().plusSeconds(subtask.getDuration() * 60));
    }

    public void checkCreateIntersection(Task task) {
        if (task.getStartTime() != null) {
            Optional<Integer> intersectionTask = Stream.of(getAllSubtasks(), getAllTasks())
                    .flatMap(List::stream)
                    .filter(t -> t.getStartTime() != null)
                    .filter(t ->
                            task.getStartTime().isAfter(t.getStartTime()) &&
                                    task.getStartTime().isBefore(t.getEndTime()) ||
                                    task.getEndTime().isAfter(t.getStartTime()) &&
                                            task.getEndTime().isBefore(t.getEndTime()) ||
                                    (task.getStartTime().equals(t.getStartTime()) &
                                            task.getEndTime().equals(t.getEndTime())))
                    .map(Task::getId)
                    .findFirst();

            if (intersectionTask.isPresent()) {
                throw new ManagerIntersectionException(String.format("Задача, которую вы хотите создать/изменить," +
                        " пересекается с задачей с id = %s.", intersectionTask.get()));
            }
        }
    }

    public void checkUpdateIntersection(Task task) {
        List<Subtask> tempSubtasks = getAllSubtasks();
        List<Task> tempTasks = getAllTasks();

        if (task.getClass() == Task.class) {
            tempTasks.remove(getTask(task.getId()));
        } else if (task.getClass() == Subtask.class) {
            tempSubtasks.remove(getSubtask(task.getId()));
        }

        Optional<Integer> intersectionTask = Stream.of(tempSubtasks, tempTasks)
                .flatMap(List::stream)
                .filter(t -> t.getStartTime() != null)
                .filter(t ->
                        task.getStartTime().isAfter(t.getStartTime()) &&
                                task.getStartTime().isBefore(t.getEndTime()) ||
                                task.getEndTime().isAfter(t.getStartTime()) &&
                                        task.getEndTime().isBefore(t.getEndTime()))
                .map(Task::getId)
                .findFirst();

        if (intersectionTask.isPresent()) {
            throw new ManagerIntersectionException(String.format("Задача, которую вы хотите создать/изменить," +
                    " пересекается с задачей с id = %s.", intersectionTask.get()));
        }
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }


}
