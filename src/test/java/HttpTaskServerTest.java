import http.*;
import http.task.create.CreateTaskDataSet;
import http.task.create.CreateTaskRequest;
import http.task.create.CreateTaskResponse;
import http.task.deleteall.DeleteAllTasksRequest;
import http.task.delete.DeleteTaskRequest;
import http.task.get.GetTaskRequest;
import http.task.get.GetTaskResponse;
import http.task.update.UpdateTaskDataSet;
import http.task.update.UpdateTaskRequest;
import http.task.update.UpdateTaskResponse;
import org.junit.jupiter.api.*;
import java.io.IOException;
import static org.assertj.core.api.Assertions.*;

public class HttpTaskServerTest {
    private static KVServer kvServer;
    private static HttpTaskServer httpTaskServer;
    CreateTaskRequest createTaskRequest = new CreateTaskRequest();
    GetTaskRequest getTaskRequest = new GetTaskRequest();
    DeleteAllTasksRequest deleteAllTasksRequest = new DeleteAllTasksRequest();
    DeleteTaskRequest deleteTaskRequest = new DeleteTaskRequest();
    UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest();

    // Шеф, прости пожалуйста. Я мучался с этим http-клиентом. В тестировании REST API никто с ним не работает
    // Поэтому я взял то, с чем работают.

    @BeforeEach
    public void startServer() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            httpTaskServer = new HttpTaskServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createdTaskTest() {
        CreateTaskDataSet task = new CreateTaskDataSet("EXE1", "payment1",
                "1691684240", 40);

        CreateTaskResponse response = createTaskRequest.createTask(task);

        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getTitle()).isEqualTo(task.getTitle());
        assertThat(response.getDescription()).isEqualTo(task.getDescription());
        assertThat(response.getStartTime()).isEqualTo(Integer.parseInt(task.getStartTime()));
        assertThat(response.getDuration()).isEqualTo(task.getDuration());
        assertThat(response.getEndTime()).isEqualTo(
                Integer.parseInt(task.getStartTime()) + task.getDuration() * 60 * 1000);
    }

    @Test
    public void getTaskTestPositive() {
        CreateTaskDataSet task = new CreateTaskDataSet("EXE1", "payment1",
                "1691684240", 40);

        createTaskRequest.createTask(task);

        GetTaskResponse response = getTaskRequest.getTaskByIdPositive(1);

        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getTitle()).isEqualTo(task.getTitle());
        assertThat(response.getDescription()).isEqualTo(task.getDescription());
        assertThat(response.getStartTime()).isEqualTo(Integer.parseInt(task.getStartTime()));
        assertThat(response.getDuration()).isEqualTo(task.getDuration());
        assertThat(response.getEndTime()).isEqualTo(
                Integer.parseInt(task.getStartTime()) + task.getDuration() * 60 * 1000);
    }

    @Test
    public void getTaskTestNegative() {
        CreateTaskDataSet task = new CreateTaskDataSet("EXE1", "payment1",
                "1691684240", 40);

        createTaskRequest.createTask(task);

        int statusCode = getTaskRequest.getTaskByIdNegative(2);

        assertThat(statusCode).isEqualTo(404);
    }

    @Test
    public void deleteTaskTestPositive() {
        CreateTaskDataSet task = new CreateTaskDataSet("EXE1", "payment1",
                "1691684240", 40);

        createTaskRequest.createTask(task);

        int deleteStatusCode = deleteTaskRequest.deleteTaskById(1);
        int getStatusCode = getTaskRequest.getTaskByIdNegative(1);

        assertThat(deleteStatusCode).isEqualTo(200);
        assertThat(getStatusCode).isEqualTo(404);
    }

    @Test
    public void deleteTaskTestNegative() {
        CreateTaskDataSet task = new CreateTaskDataSet("EXE1", "payment1",
                "1691684240", 40);

        createTaskRequest.createTask(task);

        int deleteStatusCode = deleteTaskRequest.deleteTaskById(2);

        assertThat(deleteStatusCode).isEqualTo(400);
    }

    @Test
    public void UpdateTestPositive() {
        CreateTaskDataSet task = new CreateTaskDataSet("EXE1", "payment1",
                "1691684240", 40);
        UpdateTaskDataSet updateTaskDataSet = new UpdateTaskDataSet(1, "EXE2", "payment2",
                "DONE", "1691704459818", 40);

        createTaskRequest.createTask(task);
        UpdateTaskResponse updateTaskResponse =  updateTaskRequest.updateTaskPositive(updateTaskDataSet);

        assertThat(updateTaskResponse.getId()).isEqualTo(1);
        assertThat(updateTaskResponse.getTitle()).isEqualTo(updateTaskDataSet.getTitle());
        assertThat(updateTaskResponse.getDescription()).isEqualTo(updateTaskDataSet.getDescription());
        assertThat(updateTaskResponse.getStartTime()).isEqualTo(Long.parseLong(updateTaskDataSet.getStartTime()));
        assertThat(updateTaskResponse.getDuration()).isEqualTo(updateTaskDataSet.getDuration());
        assertThat(updateTaskResponse.getEndTime()).isEqualTo(
                Long.parseLong(updateTaskDataSet.getStartTime()) + updateTaskDataSet.getDuration() * 60 * 1000);
    }

    @Test
    public void UpdateTestNegative() {
        CreateTaskDataSet task = new CreateTaskDataSet("EXE1", "payment1",
                "1691684240", 40);
        UpdateTaskDataSet updateTaskDataSet = new UpdateTaskDataSet(2, "EXE2", "payment2",
                "DONE", "1691704459818", 40);

        createTaskRequest.createTask(task);
        int statusCode =  updateTaskRequest.updateTaskNegative(updateTaskDataSet);

        assertThat(statusCode).isEqualTo(404);
    }

    @Test
    public void deleteAllTasks() {
        CreateTaskDataSet task = new CreateTaskDataSet("EXE1", "payment1",
                "1691684240", 40);

        createTaskRequest.createTask(task);

        int deleteStatusCode = deleteAllTasksRequest.deleteAllTasks();
        int getStatusCode = getTaskRequest.getTaskByIdNegative(1);

        assertThat(deleteStatusCode).isEqualTo(200);
        assertThat(getStatusCode).isEqualTo(404);
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
        httpTaskServer.stop();
    }
}
