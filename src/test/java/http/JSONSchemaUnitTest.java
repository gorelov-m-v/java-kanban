package http;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import http.server.handler.TaskHandler;
import http.server.response.Responses;
import http.server.response.PlatformResponse;
import http.task.create.CreateTaskDataSet;
import org.junit.jupiter.api.*;
public class JSONSchemaUnitTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void givenInvalidInput_whenValidating_thenInvalid() throws IOException {
            // Вызываем gson, ну ты знаешь :]
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            // Вызываем мапер. Он работает с JSON. Записывает его в POJO и читает оттуда.
            ObjectMapper mapper = new ObjectMapper();
            // Эта штука читает схему.
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
            // Это сама схема, как переменная.
            JsonSchema jsonSchema = factory.getSchema(TaskHandler.class.getResourceAsStream("/create_task_schema.json"));

            // Подопытный объект
            CreateTaskDataSet task = new CreateTaskDataSet("EXE1", "payment1",
                    "1691684240", 40);

            // Эта штука работает с памятью. Кароче, магия.
            JsonNode jsonNode = mapper.readTree(gson.toJson(task));

            // Набор ошибок
            Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);

            List<PlatformResponse> platformResponses = new ArrayList<>();
            errors.stream().forEach(error -> {
               platformResponses.add(new PlatformResponse(error.toString().substring(2)));
            });

            Responses err = null;
            if (!platformResponses.isEmpty()) {
                err = new Responses(false, 400, platformResponses);
            }

            String json = gson.toJson(err);
        System.out.println(json);

    }

    @Test
    public void givenValidInput_whenValidating_thenValid() throws IOException {

        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        JsonSchema jsonSchema = factory.getSchema(JSONSchemaUnitTest.class.getResourceAsStream("/create_task_schema.json"));

        JsonNode jsonNode = mapper.readTree(JSONSchemaUnitTest.class.getResourceAsStream("/product_valid.json"));
        Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);
        assertThat(errors).isEmpty();
    }
}
