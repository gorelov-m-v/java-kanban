import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import http.HttpTaskServer;
import http.KVServer;
import manager.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.constant.TaskStatus;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import static java.time.temporal.ChronoUnit.MINUTES;

public class Main {

    public static void main(String[] args) throws IOException {


        KVServer kvServer = new KVServer();
        kvServer.start();
        HttpTaskServer httpTaskServer = new HttpTaskServer();
    }
}
