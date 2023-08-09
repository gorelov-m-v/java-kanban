import com.google.gson.Gson;
import http.KVServer;
import manager.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.constant.TaskStatus;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Pattern;

import static java.time.temporal.ChronoUnit.MINUTES;

public class Main {

    public static void main(String[] args) throws IOException {
        new KVServer().start();
    }
}
