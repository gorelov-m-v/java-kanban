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

    public Task taskFromCSV(String str) {
        String[] values = str.split(SEPARATOR);

        int id = Integer.parseInt(values[0]);
        String type = values[1];
        String title = values[2];
        TaskStatus status = TaskStatus.valueOf(values[3]);
        String description = values[4];
        int epicId = Integer.parseInt(values[5]);

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
}
