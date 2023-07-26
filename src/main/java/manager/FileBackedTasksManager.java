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
import static java.time.temporal.ChronoUnit.MINUTES;

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

    public static void main(String[] args) {

        final Path PATH = Path.of("src/main/resources/test.csv");
        File file = new File(String.valueOf(PATH));
        FileBackedTasksManager fb = new FileBackedTasksManager(file);
//
//        Epic epic1 = new Epic("Title1", "Description1");
//        fb.createEpic(epic1);
//
        Task task1 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now().plus(50, MINUTES), 40);
        Task task2 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now().plus(100, MINUTES), 40);
        Task task3 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now().plus(150, MINUTES), 40);
        Task task4 = new Task("TestTaskTitle", "TestTaskDescription", Instant.now().plus(200, MINUTES), 40);
        Task task5 = new Task("NewTestTaskTitle", "NewTestTaskDescription", Instant.now().plus(700, MINUTES), 40);
//
        fb.createTask(task1);
//        fb.createTask(task2);
//        fb.createTask(task3);
//        fb.createTask(task4);
        fb.updateTask(1, task5);

//        System.out.println(fb.getTask(1));

//
//        fb.getTaskById(3);
//        fb.getTaskById(4);
        fb.getTaskById(1);
//        fb.getTaskById(2);
//
        FileBackedTasksManager fbNew = load(file);
//////
//////        System.out.println(fbNew.getAllEpics());
//////        System.out.println(fbNew.getAllSubtasks());
        System.out.println(fbNew.getAllTasks());
        System.out.println(fbNew.historyManager.getHistory());

        System.out.println(fb.loadFileToBuffer());

//

//        Epic epic = new Epic("TestTaskTitle", "TestTaskDescription");
//        fb.createEpic(epic);
//        Subtask subtask1 = new Subtask(
//                "TestSubtaskTitle", "TestSubtaskDescription", 1,
//                Instant.now(), 30);
//        fb.createSubtask(subtask1, 1);
//        FileBackedTasksManager fbNew = load(file);
//
//        System.out.println(fbNew.getAllEpics());
//        System.out.println(fbNew.getAllSubtasks());
//        System.out.println(fbNew.getAllTasks());
//        System.out.println(fbNew.historyManager.getHistory());


//
//        Epic epic2 = new Epic("Title2", "Description2");
//        fb.createEpic(epic2);
//
//        Subtask subtask1 = new Subtask("Title1", "Title1", epic1.getId());
//        Subtask subtask2 = new Subtask("Title2", "Title2", epic1.getId());
//        Subtask subtask3 = new Subtask("Title3", "Title3", epic1.getId());
//        fb.createSubtask(subtask1, epic1.getId());
//        fb.createSubtask(subtask2, epic1.getId());
//        fb.createSubtask(subtask3, epic1.getId());
//
//        fb.getEpicById(1);
//        fb.getSubtaskById(4);
//        fb.getTaskById(2);
//
//        System.out.println(fb.getEpic(1));
//
//        FileBackedTasksManager fbNew = load(file);
//        System.out.println(fbNew.getEpic(1));
////        System.out.println(fbNew.historyManager.getHistory());
    }
}
