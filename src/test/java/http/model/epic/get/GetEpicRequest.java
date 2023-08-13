package http.model.epic.get;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;

public class GetEpicRequest {
    public GetEpicResponse getEpicByIdPositive(int id) {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .param("id", id)
                .when()
                .get("/tasks/epic/")
                .then()
                .extract()
                .body()
                .as(GetEpicResponse.class);
    }

    public int getEpicByIdNegative(int id) {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .param("id", id)
                .when()
                .get("/tasks/epic/")
                .getStatusCode();
    }
}
