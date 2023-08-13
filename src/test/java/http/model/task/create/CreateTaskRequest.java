package http.model.task.create;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;

public class CreateTaskRequest {

    public CreateTaskResponse createTask(CreateTaskDataSet createTaskDataSet) {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .header("Content-type", "application/json")
                .and()
                .body(createTaskDataSet)
                .when()
                .post("/tasks/task/")
                .then()
                .extract()
                .body()
                .as(CreateTaskResponse.class);
    }
}
