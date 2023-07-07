package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.constants.TaskStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private static final Path PATH = Path.of("src/main/resources/test.csv");
    private File file = new File(String.valueOf(PATH));
    public static final String SEPARATOR = ",";
    public static final String HEADER = "id,type,name,status,description,epicId";

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public String taskToSCV(Task task) {
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

    public Task taskFromCSV(String str) {
        String[] values = str.split(SEPARATOR);

        int id = Integer.parseInt(values[0]);
        String type = values[1];
        String title = values[2];
        TaskStatus status = TaskStatus.valueOf(values[3]);
        String description = values[4];
        int epicId = type.equals("SUBTASK") ? Integer.parseInt(values[5]) : null;

        if (type.equals("EPIC")) {
            return new Epic(id, title, description, status);
        } else if (type.equals("TASK")) {
            return new Task(id, title, description, status);
        } else {
            return new Subtask(id, title, description, epicId, status);
        }
    }

    public String historyToCsv() {
        return historyManager.getHistory()
                .stream()
                .map(Task::getId)
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }

    public List<Integer> historyFromCSV(String valuesString) {
        if (!valuesString.equals("")) {
            String[] taskIdList = valuesString.split(SEPARATOR);
            return  Arrays.stream(taskIdList)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    public void save() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            String str = Stream.of(getAllEpics(), getAllSubtasks(), getAllTasks())
                    .flatMap(List::stream)
                    .map(this::taskToSCV)
                    .collect(Collectors.joining("\n"));
            String history = historyToCsv();

            fileWriter.write(HEADER + "\n");
            fileWriter.write(str + "\n");
            // Просто хотел попробовать что такое и как можно работать с null иначе, возможно не самое удачное место))
            Optional.of(history).ifPresent(h -> {
                try {
                    fileWriter.write(h);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

}
