package http.history;

import http.epic.get.GetEpicResponse;
import http.task.get.GetTaskResponse;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import model.Task;

import java.util.List;

import static io.restassured.RestAssured.given;

public class GetHistoryRequest {
    public List<GetTaskResponse> getHistoryRequest() {
        RestAssured.baseURI = "http://localhost";
        return given()
                .port(8080)
                .when()
                .get("/tasks/history/")
                .then()
                .extract()
                .jsonPath()
                .getList("", GetTaskResponse.class);
    }
}
