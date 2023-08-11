package http.epic.update;

import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;

public class UpdateEpicRequest {

    public UpdateEpicResponse updateEpicPositive(UpdateEpicDataSet updateTaskDataSet) {
        RestAssured.baseURI = "http://localhost";
        return given()

                .port(8080)
                .header("Content-type", "application/json")
                .and()
                .body(updateTaskDataSet)
                .when()
                .put("/tasks/epic/")
                .then()
                .extract()
                .body()
                .as(UpdateEpicResponse.class);
    }

    public int updateEpicNegative(UpdateEpicDataSet updateTaskDataSet) {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .header("Content-type", "application/json")
                .and()
                .body(updateTaskDataSet)
                .when()
                .put("/tasks/epic/")
                .getStatusCode();
    }
}
