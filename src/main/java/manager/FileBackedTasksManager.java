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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private static final Path PATH = Path.of(
            "src" +File.separator +
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
                if (history != null) {
                    for (Integer historyPoint : history) {
                        if (fileManager.tasks.get(historyPoint) != null) {
                            fileManager.historyManager.add(fileManager.tasks.get(historyPoint));
                        } else if (fileManager.epics.get(historyPoint) != null) {
                            fileManager.historyManager.add(fileManager.epics.get(historyPoint));
                        } else if (fileManager.subtasks.get(historyPoint) != null){
                            fileManager.historyManager.add(fileManager.subtasks.get(historyPoint));
                        }
                    }
                }
            }
        }
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
        if (task != null) {
            historyManager.add(task);
        }
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        save();
        return epic;
    }

    @Override
    public Task getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task task, String title, String description, TaskStatus status) {
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        tasks.put(task.getId(), task);

        save();
    }

    @Override
    public void updateEpic(Epic epic, String title, String description) {
        epic.setTitle(title);
        epic.setDescription(description);
        epics.put(epic.getId(), epic);

        save();
    }

    @Override
    public void updateSubtask(int subtaskId, Subtask newSubtaskData) {
        newSubtaskData.setId(subtaskId);

        int epicId = getEpicBySubtaskId(subtaskId).getId();

        epics.get(epicId).removeSubtask(subtaskId);
        epics.get(epicId).getSubtasks().add(newSubtaskData.getId());

        checkEpicStatus(getEpicBySubtaskId(subtaskId));

        save();
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
        List<Subtask> subtasks = getAllSubtasks();
        Task subtask = getSubtaskById(id);
        if (subtasks.contains(subtask)) {
            getEpicBySubtaskId(id).removeSubtask(id);
            historyManager.removeById(id);
        }
        save();
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


        FileBackedTasksManager fbNew = load(file);
        System.out.println(fbNew.historyManager.getHistory());
    }
}
