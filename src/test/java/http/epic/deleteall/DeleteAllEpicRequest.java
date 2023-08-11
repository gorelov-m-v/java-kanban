package http.epic.deleteall;

import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;

public class DeleteAllEpicRequest {
    public int deleteAllEpics() {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .when()
                .delete("/tasks/epic/")
                .getStatusCode();
    }
}
