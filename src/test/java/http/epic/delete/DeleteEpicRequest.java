package http.epic.delete;

import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;

public class DeleteEpicRequest {

    public int deleteEpicById(int id) {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .param("id", id)
                .when()
                .delete("/tasks/epic/")
                .getStatusCode();
    }
}
