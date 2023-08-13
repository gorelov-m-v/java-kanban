package http.model.task.prioritized;

import http.model.task.get.GetTaskResponse;
import io.restassured.RestAssured;

import java.util.List;

import static io.restassured.RestAssured.given;

public class GetPrioritizedTasksRequest {

    public List<GetTaskResponse> getPrioritizedTasks() {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .when()
                .get("/tasks/")
                .then()
                .extract()
                .jsonPath()
                .getList("", GetTaskResponse.class);
    }
}
