package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.constants.TaskStatus;
import model.exceptions.ManagerSaveException;

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
    private static final Path PATH = Path.of("src/main/resources/test.csv");
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
        String[] attributes = line.split(",");

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

    private List<Integer> historyFromCSV(String valuesString) {
        if (!valuesString.equals("")) {
            String[] taskIdList = valuesString.split(SEPARATOR);
            return Arrays.stream(taskIdList)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    private void save() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            String str = Stream.of(getAllEpics(), getAllSubtasks(), getAllTasks())
                    .flatMap(List::stream)
                    .map(this::taskToSCV)
                    .collect(Collectors.joining(",\n"));
            String history = historyToCsv(historyManager);

            fileWriter.write(HEADER + "\n");
            fileWriter.write(str + "\n");
            fileWriter.write("\n");
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

    private List<String> loadFileToBuffer() {
        try (BufferedReader br = Files.newBufferedReader(PATH, StandardCharsets.UTF_8)) {
            br.readLine();
            String line = br.readLine();
            List<String> lines = new ArrayList<>();
            while (line != null) {
                lines.add(line);
                line = br.readLine();
            }
            return lines;
        } catch (IOException e) {
            throw new ManagerSaveException("Не вышло :[", e);
        }
    }

    private static FileBackedTasksManager load(File file) {
        FileBackedTasksManager fb = new FileBackedTasksManager(file);

        List<String> lines = fb.loadFileToBuffer();

        for (int i = 0; i <= lines.size(); i++) {
            if (i < lines.size() - 2) {
                Task task = fb.taskFromCSV(lines.get(i));
                if (task.getClass() == Task.class) {
                    fb.tasks.put(task.getId(), task);
                } else if (task.getClass() == Epic.class) {
                    fb.epics.put(task.getId(), (Epic) task);
                } else {
                    fb.subtasks.put(task.getId(), (Subtask) task);
                }
            } else if (i == lines.size() - 1 && lines.get(i) != null) {
                List<Integer> history = fb.historyFromCSV(lines.get(i));
                for (Integer historyPoint : history) {
                    if (fb.tasks.get(historyPoint) != null) {
                        fb.historyManager.add(fb.tasks.get(historyPoint));
                    } else if (fb.epics.get(historyPoint) != null) {
                        fb.historyManager.add(fb.epics.get(historyPoint));
                    } else if (fb.subtasks.get(historyPoint) != null){
                        fb.historyManager.add(fb.subtasks.get(historyPoint));
                    }
                }
            }
        }
        return fb;
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

    public static void main(String[] args) {

        final Path PATH = Path.of("src/main/resources/test.csv");
        File file = new File(String.valueOf(PATH));
        FileBackedTasksManager fb = load(file);

        Epic epic = new Epic("Title1", "Description1");

        Task task1 = new Task("Test1", "Test1");
        Epic epic2 = new Epic("Title1", "Description1");
        Subtask subtask = new Subtask("Title", "Title", epic.getId(), TaskStatus.NEW);
        Subtask subtask1 = new Subtask("Title1", "Title1", epic.getId(), TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Title2", "Title2", epic.getId(), TaskStatus.NEW);

//        id,type,name,status,description,epicId
//        1,EPIC,Title1,NEW,Description1,,
//        2,SUBTASK,Title,NEW,Title,1,
//                3,SUBTASK,Title2,NEW,Title2,1
//
//        1,2,3


    }
}
