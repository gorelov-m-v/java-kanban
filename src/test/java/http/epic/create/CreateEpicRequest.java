package http.epic.create;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;

public class CreateEpicRequest {

    public CreateEpicResponse createTask(CreateEpicDataSet createTaskDataSet) {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .header("Content-type", "application/json")
                .and()
                .body(createTaskDataSet)
                .when()
                .post("/tasks/epic/")
                .then()
                .extract()
                .body()
                .as(CreateEpicResponse.class);
    }
}
