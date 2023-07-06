package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.constants.TaskStatus;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private static final Path PATH = Path.of("test.csv");
    private File file = new File(String.valueOf(PATH));
    public static final String SEPARATOR = ",";

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
                String.valueOf(getEpicBySubtaskId(task.getId()))
        };
        return String.join(SEPARATOR, arr);
    }

    public Task taskFromCSV(String valuesString) {
        String[] valuesArray = valuesString.split(SEPARATOR);
        if (valuesArray[1].equals("EPIC")) {
            Epic epic = new Epic(valuesArray[2], valuesArray[4]);
            epic.setId(Integer.parseInt(valuesArray[0]));
            epic.setStatus(TaskStatus.valueOf(valuesArray[3]));
            return epic;
        } else if (valuesArray[1].equals("TASK")) {
            Task task = new Task(valuesArray[2], valuesArray[4]);
            task.setId(Integer.parseInt(valuesArray[0]));
            task.setStatus(TaskStatus.valueOf(valuesArray[3]));
            return task;
        } else {
            Subtask subtask = new Subtask(valuesArray[2], valuesArray[4], Integer.parseInt(valuesArray[5]));
            subtask.setId(Integer.parseInt(valuesArray[0]));
            subtask.setStatus(TaskStatus.valueOf(valuesArray[3]));
            return subtask;
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
        if (valuesString != "") {
            String[] taskIdList = valuesString.split(SEPARATOR);
            return  Arrays.stream(taskIdList)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }
}
