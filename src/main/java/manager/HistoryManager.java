package manager;

import model.Task;
import java.util.List;
import java.util.function.Consumer;

public interface HistoryManager {

    void add(Task task);
    void removeById(int id);
    List<Task> getHistory();
}
