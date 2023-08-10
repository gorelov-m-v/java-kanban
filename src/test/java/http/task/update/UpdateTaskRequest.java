package http.task.update;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;

public class UpdateTaskRequest {

    public UpdateTaskResponse updateTaskPositive(UpdateTaskDataSet updateTaskDataSet) {
        RestAssured.baseURI = "http://localhost";
        return given()

                .port(8080)
                .header("Content-type", "application/json")
                .and()
                .body(updateTaskDataSet)
                .when()
                .put("/tasks/task/")
                .then()
                .extract()
                .body()
                .as(UpdateTaskResponse.class);
    }

    public int updateTaskNegative(UpdateTaskDataSet updateTaskDataSet) {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .header("Content-type", "application/json")
                .and()
                .body(updateTaskDataSet)
                .when()
                .put("/tasks/task/")
                .getStatusCode();
    }
}
