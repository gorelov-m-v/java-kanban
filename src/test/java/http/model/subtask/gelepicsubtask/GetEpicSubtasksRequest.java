package http.model.subtask.gelepicsubtask;

import http.model.subtask.get.GetSubtaskResponse;
import io.restassured.RestAssured;

import java.util.List;

import static io.restassured.RestAssured.given;

public class GetEpicSubtasksRequest {

    public List<GetSubtaskResponse> getEpicSubtasksRequestPositive(int id) {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .param("id", id)
                .when()
                .get("/tasks/subtask/epic/")
                .then()
                .extract()
                .body()
                .jsonPath()
                .getList("", GetSubtaskResponse.class);

    }

    public int getEpicSubtasksRequestNegative(int id) {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .param("id", id)
                .when()
                .get("/tasks/subtask/epic/")
                .getStatusCode();
    }
}
