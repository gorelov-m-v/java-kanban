package http.task.delete;

import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;

public class DeleteTaskRequest {

    public int deleteTaskById(int id) {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .param("id", id)
                .when()
                .delete("/tasks/task/")
                .getStatusCode();
    }
}
