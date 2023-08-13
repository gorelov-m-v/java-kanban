package http.model.subtask.get;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;

public class GetSubtaskRequest {
    public GetSubtaskResponse getSubtaskByIdPositive(int id) {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .param("id", id)
                .when()
                .get("/tasks/subtask/")
                .then()
                .extract()
                .body()
                .as(GetSubtaskResponse.class);
    }

    public int getSubtaskByIdNegative(int id) {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .param("id", id)
                .when()
                .get("/tasks/subtask/")
                .getStatusCode();
    }
}
