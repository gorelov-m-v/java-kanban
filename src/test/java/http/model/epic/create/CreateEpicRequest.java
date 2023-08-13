package http.model.epic.create;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;

public class CreateEpicRequest {

    public CreateEpicResponse createEpic(CreateEpicDataSet createEpicDataSet) {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .header("Content-type", "application/json")
                .and()
                .body(createEpicDataSet)
                .when()
                .post("/tasks/epic/")
                .then()
                .extract()
                .body()
                .as(CreateEpicResponse.class);
    }
}
