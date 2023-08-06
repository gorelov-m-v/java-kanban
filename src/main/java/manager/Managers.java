package manager;

import java.io.File;

public class Managers {

    public static InMemoryTaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedManager(File file) {
        return new FileBackedTasksManager(file);
    }
}
