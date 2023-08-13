package http.model.subtask.deleteall;

import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;

public class DeleteAllSubtaskRequest {
    public int deleteAllSubtasks() {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .when()
                .delete("/tasks/subtask/")
                .getStatusCode();
    }
}
