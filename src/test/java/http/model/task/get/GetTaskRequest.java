package http.model.task.get;

import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;

public class GetTaskRequest {
    public GetTaskResponse getTaskByIdPositive(int id) {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .param("id", id)
                .when()
                .get("/tasks/task/")
                .then()
                .extract()
                .body()
                .as(GetTaskResponse.class);
    }

    public int getTaskByIdNegative(int id) {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .param("id", id)
                .when()
                .get("/tasks/task/")
                .getStatusCode();
    }
}
