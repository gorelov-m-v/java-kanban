import http.*;
import http.epic.create.CreateEpicDataSet;
import http.epic.create.CreateEpicRequest;
import http.epic.create.CreateEpicResponse;
import http.epic.delete.DeleteEpicRequest;
import http.epic.deleteall.DeleteAllEpicRequest;
import http.epic.get.GetEpicRequest;
import http.epic.get.GetEpicResponse;
import http.epic.update.UpdateEpicDataSet;
import http.epic.update.UpdateEpicRequest;
import http.epic.update.UpdateEpicResponse;
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
    CreateEpicRequest createEpicRequest = new CreateEpicRequest();
    GetEpicRequest getEpicRequest = new GetEpicRequest();
    DeleteEpicRequest deleteEpicRequest = new DeleteEpicRequest();
    DeleteAllEpicRequest deleteAllEpicRequest = new DeleteAllEpicRequest();
    UpdateEpicRequest updateEpicRequest = new UpdateEpicRequest();

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
    public void createTaskTest() {
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

    @Test
    public void createEpicTest() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");

        CreateEpicResponse response = createEpicRequest.createEpic(epic);

        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getTitle()).isEqualTo(epic.getTitle());
        assertThat(response.getDescription()).isEqualTo(epic.getDescription());
        assertThat(response.getStartTime()).isEqualTo(0);
        assertThat(response.getDuration()).isEqualTo(0);
        assertThat(response.getEndTime()).isEqualTo(0);
    }

    @Test
    public void getEpicTest() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");

        createEpicRequest.createEpic(epic);

        GetEpicResponse getEpicResponse = getEpicRequest.getEpicByIdPositive(1);

        assertThat(getEpicResponse.getId()).isEqualTo(1);
        assertThat(getEpicResponse.getTitle()).isEqualTo(epic.getTitle());
        assertThat(getEpicResponse.getDescription()).isEqualTo(epic.getDescription());
        assertThat(getEpicResponse.getStartTime()).isEqualTo(0);
        assertThat(getEpicResponse.getDuration()).isEqualTo(0);
        assertThat(getEpicResponse.getEndTime()).isEqualTo(0);
    }

    @Test
    public void deleteEpicTestPositive() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");

        createEpicRequest.createEpic(epic);

        int deleteStatusCode = deleteEpicRequest.deleteEpicById(1);
        int getStatusCode = getEpicRequest.getEpicByIdNegative(1);

        assertThat(deleteStatusCode).isEqualTo(200);
        assertThat(getStatusCode).isEqualTo(404);
    }

    @Test
    public void deleteEpicTestNegative() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");

        createEpicRequest.createEpic(epic);

        int deleteStatusCode = deleteEpicRequest.deleteEpicById(2);

        assertThat(deleteStatusCode).isEqualTo(400);
    }

    @Test
    public void deleteAllEpics() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");

        createEpicRequest.createEpic(epic);

        int deleteStatusCode = deleteAllEpicRequest.deleteAllEpics();

        assertThat(deleteStatusCode).isEqualTo(200);
    }

    @Test
    public void UpdateEpicPositive() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");
        UpdateEpicDataSet updateEpicDataSet = new UpdateEpicDataSet(1, "EXE2", "payment2");

        createEpicRequest.createEpic(epic);
        UpdateEpicResponse updateEpicResponse =  updateEpicRequest.updateEpicPositive(updateEpicDataSet);

        assertThat(updateEpicResponse.getId()).isEqualTo(1);
        assertThat(updateEpicResponse.getTitle()).isEqualTo(updateEpicDataSet.getTitle());
        assertThat(updateEpicResponse.getDescription()).isEqualTo(updateEpicDataSet.getDescription());
        assertThat(updateEpicResponse.getStartTime()).isEqualTo(0);
        assertThat(updateEpicResponse.getDuration()).isEqualTo(0);
        assertThat(updateEpicResponse.getEndTime()).isEqualTo(0);
    }

    @Test
    public void UpdateEpicNegative() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");
        UpdateEpicDataSet updateEpicDataSet = new UpdateEpicDataSet(2, "EXE2", "payment2");

        createEpicRequest.createEpic(epic);
        int statusCode =  updateEpicRequest.updateEpicNegative(updateEpicDataSet);

        assertThat(statusCode).isEqualTo(404);
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
        httpTaskServer.stop();
    }
}
