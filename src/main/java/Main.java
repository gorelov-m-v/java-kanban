import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
        // Сгенерировал строку
        List<Integer> history = List.of(1, 2, 3, 4, 5, 6, 7);
        Gson gson = new Gson();
        String value1 = gson.toJson(history);

        // Вот этот метод
        List<Integer> history1 = gson.fromJson(value1, new TypeToken<>(){});


        // Вывод
        System.out.println(history1);
    }
}
