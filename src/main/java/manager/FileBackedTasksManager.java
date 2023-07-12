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
import java.sql.SQLOutput;
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
                task.getEpicId(task)
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

        if (type.equals("EPIC")) {
            return new Epic(id, title, description, status);
        } else if (type.equals("TASK")) {
            return new Task(id, title, description, status);
        } else {
            return new Subtask(id, title, description, epicId, status);
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
            fileWriter.write("\n");
            // Просто хотел попробовать что такое и как можно работать с null иначе, возможно не самое удачное место))
            Optional.of(history).ifPresent(h -> {
                try {
                    fileWriter.write(h);
                } catch (IOException e) {
                    throw new ManagerSaveException("Не вышло сохранить файл:[", e);
                }
            });
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

    private static FileBackedTasksManager load(File file) {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);
        List<String> lines = fileManager.loadFileToBuffer();

        for (int i = 0; i <= lines.size(); i++) {
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
        fileManager.subtasks.values().forEach(s -> {
            fileManager.getEpicById(s.getEpicId()).getSubtasks().add(s.getId());
        });
        return fileManager;
    }

    @Override
    public void createTask(Task task) {
        task.setId(getNewId());
        tasks.put(task.getId(), task);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask, int epicId) {
        subtask.setId(getNewId());

        getEpic(epicId).addSubtask(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(getNewId());
        epics.put(epic.getId(), epic);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        Optional<Task> optionalTask = Optional.ofNullable(task);

        optionalTask.ifPresent(t -> {
            historyManager.add(t);
            save();
        });

        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        Optional<Task> optionalTask = Optional.ofNullable(epic);

        optionalTask.ifPresent(s -> {
            historyManager.add(s);
            save();
        });

        return epic;
    }

    @Override
    public Task getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        Optional<Task> optionalTask = Optional.ofNullable(subtask);

        optionalTask.ifPresent(s -> {
            historyManager.add(s);
            save();
        });

        return subtask;
    }

    @Override
    public void updateTask(Task task, String title, String description, TaskStatus status) {
        Optional<Task> optionalTask = Optional.ofNullable(task);

        optionalTask.ifPresent(t -> {
            t.setTitle(title);
            t.setDescription(description);
            t.setStatus(status);
            tasks.put(t.getId(), t);
            save();
        });
    }

    @Override
    public void updateEpic(Epic epic, String title, String description) {
        Optional<Epic> optionalTask = Optional.ofNullable(epic);

        optionalTask.ifPresent(e -> {
            e.setTitle(title);
            e.setDescription(description);
            epics.put(e.getId(), e);
            save();
        });
    }

    @Override
    public void updateSubtask(int subtaskId, Subtask newSubtaskData) {
        newSubtaskData.setId(subtaskId);
        Optional<Epic> epicOptional = Optional.ofNullable(getEpicBySubtaskId(subtaskId));

        epicOptional.ifPresent(e -> {
            subtasks.remove(subtaskId);
            subtasks.put(subtaskId, newSubtaskData);

            checkEpicStatus(getEpicBySubtaskId(subtaskId));
            save();
        });
    }

    @Override
    public void removeTaskById(int id) {
        historyManager.removeById(id);

        tasks.remove(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        getAllSubtasksFromEpic(id)
                .forEach(subtaskId -> historyManager.removeById(subtaskId));

        historyManager.removeById(id);

        epics.remove(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        Optional<Task> optionalSubtask = Optional.ofNullable(getSubtaskById(id));

        optionalSubtask.ifPresent(s -> {
            getEpicBySubtaskId(id).removeSubtask(id);
            historyManager.removeById(id);
            save();
        });
    }

    public static void main(String[] args) {

        final Path PATH = Path.of("src/main/resources/test.csv");
        File file = new File(String.valueOf(PATH));
        FileBackedTasksManager fb = new FileBackedTasksManager(file);

        Epic epic1 = new Epic("Title1", "Description1");
        fb.createEpic(epic1);

        Task task1 = new Task("Test1", "Test1");
        fb.createTask(task1);

        Epic epic2 = new Epic("Title2", "Description2");
        fb.createEpic(epic2);

        Subtask subtask1 = new Subtask("Title1", "Title1", epic1.getId(), TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Title2", "Title2", epic1.getId(), TaskStatus.NEW);
        Subtask subtask3 = new Subtask("Title3", "Title3", epic1.getId(), TaskStatus.NEW);
        fb.createSubtask(subtask1, epic1.getId());
        fb.createSubtask(subtask2, epic1.getId());
        fb.createSubtask(subtask3, epic1.getId());

        fb.getEpicById(1);
        fb.getSubtaskById(4);
        fb.getTaskById(2);

        System.out.println(fb.getEpic(1));

        FileBackedTasksManager fbNew = load(file);
        System.out.println(fbNew.getEpic(1));
//        System.out.println(fbNew.historyManager.getHistory());
    }
}
