package http.client;

import com.google.gson.JsonParser;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static model.constant.Keys.*;

public class KVTaskClient {

    private String API_TOKEN;
    private final String url;
    private final List<String> availableKeys = List.of(
            TASKS.getKey(), EPICS.getKey(), SUBTASKS.getKey(), HISTORY.getKey());

    public KVTaskClient(String url) {
        this.url = url;
        register();
    }

    public void register() {
        try {
            URI uri = new URIBuilder(url + "/register")
                    .build();

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .header("Content-Type", "application/json")
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            API_TOKEN = response.body();
        } catch (IOException | InterruptedException | URISyntaxException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public void save(String key, String value){
        if (!API_TOKEN.isEmpty()) {
            System.out.println("Стоит авторизоваться");
        }
        if (!availableKeys.contains(key)) {
            System.out.println("Выбран неверный ключ");
        }
        if (isValid(value)) {
            System.out.println("Передан невалидный JSON");
        }

        try {
            URI uri = new URIBuilder(url + "/save/" + key)
                    .addParameter("API_TOKEN", API_TOKEN)
                    .build();

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(value))
                    .header("Content-Type", "application/json")
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> response = httpClient.send(httpRequest,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        } catch (IOException | InterruptedException | URISyntaxException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public String load(String key) {
        String value;
        try {
            URI uri = new URIBuilder(url + "/load/" + key)
                    .addParameter("API_TOKEN", API_TOKEN)
                    .build();

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .header("Content-Type", "application/json")
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> response = httpClient.send(httpRequest,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            value = response.body();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            value = "";
        }
        return value;
    }

    private boolean isValid(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }
}
