package manager;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import http.server.adapter.InstantAdapter;
import http.client.KVTaskClient;
import model.Epic;
import model.Subtask;
import model.Task;
import model.constant.Keys;
import com.google.gson.JsonParser;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static model.constant.Keys.*;

public class HttpTaskManager extends FileBackedTasksManager {

    private final KVTaskClient kvTaskClient;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();

    public HttpTaskManager(String url) {
        super(url);
        this.kvTaskClient = new KVTaskClient(url);
    }

    @Override
    public void save() {
        kvTaskClient.save(TASKS.getKey(), gson.toJson(tasks.values()));
        kvTaskClient.save(Keys.SUBTASKS.getKey(), gson.toJson(subtasks.values()));
        kvTaskClient.save(Keys.EPICS.getKey(), gson.toJson(epics.values()));
        List<Integer> history = historyManager.getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList());
        kvTaskClient.save(Keys.HISTORY.getKey(), gson.toJson(history));
    }

    public void loadByKey(Keys key) {
        String value = kvTaskClient.load(key.getKey());
        JsonElement jsonValue = JsonParser.parseString(value);
        if (jsonValue.isJsonNull()) {
            return;
        }
        JsonArray jsonValueArray = jsonValue.getAsJsonArray();
        for (JsonElement element : jsonValueArray) {
            switch (key) {
                case TASKS:
                    Task task = gson.fromJson(element, Task.class);
                    tasks.put(task.getId(), task);
                    prioritizedTasks.add(task);
                    break;
                case EPICS:
                    Epic epic = gson.fromJson(element, Epic.class);
                    epics.put(epic.getId(), epic);
                    break;
                case SUBTASKS:
                    Subtask subtask = gson.fromJson(element, Subtask.class);
                    subtasks.put(subtask.getId(), subtask);
                    prioritizedTasks.add(subtask);
                    break;
                case HISTORY:
                    List<Integer> history = gson.fromJson(value, new TypeToken<>() {
                    });
                    if (!history.isEmpty()) {
                        history.forEach(i ->
                        {
                            if (epics.containsKey(i)) {
                                historyManager.add(epics.get(i));
                            } else if (tasks.containsKey(i)) {
                                historyManager.add(tasks.get(i));
                            } else {
                                historyManager.add(subtasks.get(i));
                            }
                        });
                    }
            }
        }
    }
}
