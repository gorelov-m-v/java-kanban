package http.model.subtask.update;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;

public class UpdateSubtaskRequest {

    public UpdateSubtaskResponse updateSubtaskPositive(UpdateSubtaskDataSet updateSubtaskDataSet) {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .header("Content-type", "application/json")
                .and()
                .body(updateSubtaskDataSet)
                .when()
                .put("/tasks/subtask/")
                .then()
                .extract()
                .body()
                .as(UpdateSubtaskResponse.class);
    }

    public int updateSubtaskNegative(UpdateSubtaskDataSet updateSubtaskDataSet) {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .header("Content-type", "application/json")
                .and()
                .body(updateSubtaskDataSet)
                .when()
                .put("/tasks/subtask/")
                .getStatusCode();
    }
}
