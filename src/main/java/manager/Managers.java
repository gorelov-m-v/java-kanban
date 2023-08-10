package manager;

import java.io.File;

public class Managers {

    public static HttpTaskManager getDefaultTaskManager() {
        return new HttpTaskManager("http://localhost:10000");
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedManager(File file) {
        return new FileBackedTasksManager(file);
    }
}
