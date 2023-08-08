package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.constant.TaskStatus;
import model.exception.ManagerSaveException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private static final Path PATH = Path.of(
            "src" + File.separator +
                    "main" + File.separator +
                    "resources" + File.separator +
                    "test.csv");
    private File file = new File(String.valueOf(PATH));
    private static final String SEPARATOR = ",";
    private static final String HEADER = "id,type,name,status,description,epicId";

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    private String taskToSCV(Task task) {
        String[] arr = new String[]{
                String.valueOf(task.getId()),
                getType(task).name(),
                task.getTitle(),
                task.getStatus().name(),
                task.getDescription(),
                task.getEpicId(task),
                String.valueOf(task.getStartTime()),
                String.valueOf(task.getDuration())
        };
        return String.join(SEPARATOR, arr);
    }

    private Task taskFromCSV(String line) {
        String[] attributes = line.split(SEPARATOR);

        int id = Integer.parseInt(attributes[0]);
        String type = attributes[1];
        String title = attributes[2];
        TaskStatus status = TaskStatus.valueOf(attributes[3]);
        String description = attributes[4];
        int epicId = type.equals("SUBTASK") ? Integer.parseInt(attributes[5]) : 0;
        Instant startTime = attributes[6].equals("null") ? null : Instant.parse(attributes[6]);
        long duration = Long.parseLong(attributes[7]);

        if (type.equals("EPIC")) {
            return new Epic(id, title, description, status, startTime, duration);
        } else if (type.equals("TASK")) {
            return new Task(id, title, description, status, startTime, duration);
        } else {
            return new Subtask(id, title, description, epicId, status, startTime, duration);
        }
    }

    static private String historyToCsv(HistoryManager historyManager) {
        return historyManager.getHistory()
                .stream()
                .map(Task::getId)
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }

    private static List<Integer> historyFromCSV(String valuesString) {
        if (!valuesString.equals("")) {
            String[] taskIdList = valuesString.split(SEPARATOR);
            return Arrays.stream(taskIdList)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    private void save() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            String str = Stream.of(getAllEpics(), getAllSubtasks(), getAllTasks())
                    .flatMap(List::stream)
                    .sorted(Comparator.comparingInt(Task::getId))
                    .map(this::taskToSCV)
                    .collect(Collectors.joining(SEPARATOR + "\n"));

            String history = historyToCsv(historyManager);

            fileWriter.write(HEADER + "\n");
            fileWriter.write(str + "\n");
            // Просто хотел попробовать что такое и как можно работать с null иначе, возможно не самое удачное место))
            try {
                fileWriter.write("\n");
                fileWriter.write(history);
            } catch (IOException e) {
                throw new ManagerSaveException("Не вышло сохранить файл:[", e);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не вышло сохранить файл:[", e);
        }
    }

    private List<String> loadFileToBuffer() {
        try (BufferedReader bufferedReader = Files.newBufferedReader(PATH, StandardCharsets.UTF_8)) {
            bufferedReader.readLine();
            String line = bufferedReader.readLine();
            List<String> lines = new ArrayList<>();
            while (line != null) {
                lines.add(line);
                line = bufferedReader.readLine();
            }
            return lines;
        } catch (IOException e) {
            throw new ManagerSaveException("Не вышло загрузить файл:[", e);
        }
    }

    public static FileBackedTasksManager load(File file) {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);
        List<String> lines = fileManager.loadFileToBuffer();

        if (lines.get(lines.size() - 1).equals("")) {
            for (int i = 0; i < lines.size(); i++) {
                if (i < lines.size() - 1) {
                    Task task = fileManager.taskFromCSV(lines.get(i));
                    if (task.getClass() == Task.class) {
                        fileManager.tasks.put(task.getId(), task);
                    } else if (task.getClass() == Epic.class) {
                        fileManager.epics.put(task.getId(), (Epic) task);
                    } else {
                        fileManager.subtasks.put(task.getId(), (Subtask) task);
                    }
                }
            }
        } else {
            for (int i = 0; i < lines.size(); i++) {
                if (i < lines.size() - 2) {
                    Task task = fileManager.taskFromCSV(lines.get(i));
                    if (task.getClass() == Task.class) {
                        fileManager.tasks.put(task.getId(), task);
                    } else if (task.getClass() == Epic.class) {
                        fileManager.epics.put(task.getId(), (Epic) task);
                    } else {
                        fileManager.subtasks.put(task.getId(), (Subtask) task);
                    }
                } else if (i == lines.size() - 1 && lines.get(i) != null) {
                    List<Integer> history = historyFromCSV(lines.get(i));
                    for (Integer historyPoint : history) {
                        if (fileManager.tasks.get(historyPoint) != null) {
                            fileManager.historyManager.add(fileManager.tasks.get(historyPoint));
                        } else if (fileManager.epics.get(historyPoint) != null) {
                            fileManager.historyManager.add(fileManager.epics.get(historyPoint));
                        } else if (fileManager.subtasks.get(historyPoint) != null) {
                            fileManager.historyManager.add(fileManager.subtasks.get(historyPoint));
                        }
                    }
                }
            }
        }
        fileManager.subtasks.values().forEach(s -> {
            fileManager.getEpicById(s.getEpicId()).getSubtasks().add(s.getId());
        });
        fileManager.subtasks.values().forEach(fileManager::updateSubTaskEndTime);
        fileManager.tasks.values().forEach(fileManager::updateTaskEndTime);
        fileManager.epics.values().forEach(fileManager::updateEpicTime);

        return fileManager;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask, int epicId) {
        super.createSubtask(subtask, epicId);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();

        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();

        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();

        return subtask;
    }

    @Override
    public void updateTask(Integer taskId, Task newTaskData) {
        super.updateTask(taskId, newTaskData);
        save();
    }

    @Override
    public void updateEpic(Integer epicId, Epic newEpicData) {
        super.updateEpic(epicId, newEpicData);
        save();
    }

    @Override
    public void updateSubtask(Integer subtaskId, Subtask newSubtaskData) {
        super.updateSubtask(subtaskId, newSubtaskData);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

}
