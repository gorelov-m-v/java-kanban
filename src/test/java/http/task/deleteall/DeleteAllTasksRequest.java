package http.task.deleteall;

import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;

public class DeleteAllTasksRequest {
    public int deleteAllTasks() {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .when()
                .delete("/tasks/task/")
                .getStatusCode();
    }
}
