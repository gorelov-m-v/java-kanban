package http.subtask.delete;

import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;

public class DeleteSubtaskRequest {

    public int deleteSubtaskById(int id) {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .param("id", id)
                .when()
                .delete("/tasks/subtask/")
                .getStatusCode();
    }
}
