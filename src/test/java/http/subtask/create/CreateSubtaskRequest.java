package http.subtask.create;

import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;

public class CreateSubtaskRequest {

    public CreateSubtaskResponse createSubtask(CreateSubtaskDataSet createSubtaskDataSet, int id) {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .queryParam("id", id)
                .header("Content-type", "application/json")
                .and()
                .body(createSubtaskDataSet)
                .when()
                .post("/tasks/subtask/epic/")
                .then()
                .extract()
                .body()
                .as(CreateSubtaskResponse.class);
    }
}
