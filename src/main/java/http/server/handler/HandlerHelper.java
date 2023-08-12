package http.server.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.sun.net.httpserver.HttpExchange;
import http.server.response.Responses;
import http.server.response.PlatformResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HandlerHelper {
    public String getJsonError(String requestBody, String jsonSchema) throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        ObjectMapper mapper = new ObjectMapper();
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        JsonSchema schema = factory.getSchema(TaskHandler.class.getResourceAsStream(jsonSchema));
        JsonNode jsonNode = mapper.readTree(requestBody);

        Set<ValidationMessage> errors = schema.validate(jsonNode);
        List<PlatformResponse> platformResponses = new ArrayList<>();
        errors.stream().forEach(error -> {
            platformResponses.add(new PlatformResponse(error.toString().substring(2)));
        });

        Responses err = null;
        if (!platformResponses.isEmpty()) {
            err = new Responses(false, 400, platformResponses);
        }

        return gson.toJson(err);
    }

    public boolean validateJson(String requestBody, String jsonSchema) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        JsonSchema schema = factory.getSchema(TaskHandler.class.getResourceAsStream(jsonSchema));
        JsonNode jsonNode = mapper.readTree(requestBody);

        Set<ValidationMessage> errors = schema.validate(jsonNode);
        if (errors.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public Responses constructResponse(boolean success, int code, String message) {
        return new Responses(success, code, List.of(
                new PlatformResponse(message)));
    }

    public int getIdFromPath(HttpExchange exchange) {
        return Integer.parseInt(exchange.getRequestURI().getQuery().split("=")[1]);
    }

    public boolean isTotalDelete(HttpExchange exchange) {
        return exchange.getRequestURI().getQuery() == null;
    }
}
