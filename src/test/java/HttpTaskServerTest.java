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
import http.history.GetHistoryRequest;
import http.subtask.create.CreateSubtaskDataSet;
import http.subtask.create.CreateSubtaskRequest;
import http.subtask.create.CreateSubtaskResponse;
import http.subtask.delete.DeleteSubtaskRequest;
import http.subtask.deleteall.DeleteAllSubtaskRequest;
import http.subtask.gelepicsubtask.GetEpicSubtasksRequest;
import http.subtask.get.GetSubtaskRequest;
import http.subtask.get.GetSubtaskResponse;
import http.subtask.update.UpdateSubtaskDataSet;
import http.subtask.update.UpdateSubtaskRequest;
import http.subtask.update.UpdateSubtaskResponse;
import http.task.create.CreateTaskDataSet;
import http.task.create.CreateTaskRequest;
import http.task.create.CreateTaskResponse;
import http.task.deleteall.DeleteAllTasksRequest;
import http.task.delete.DeleteTaskRequest;
import http.task.get.GetTaskRequest;
import http.task.get.GetTaskResponse;
import http.task.prioritized.GetPrioritizedTasksRequest;
import http.task.update.UpdateTaskDataSet;
import http.task.update.UpdateTaskRequest;
import http.task.update.UpdateTaskResponse;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.util.List;

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
    CreateSubtaskRequest createSubtaskRequest = new CreateSubtaskRequest();
    GetSubtaskRequest getSubtaskRequest = new GetSubtaskRequest();
    DeleteSubtaskRequest deleteSubtaskRequest = new DeleteSubtaskRequest();
    DeleteAllSubtaskRequest deleteAllSubtaskRequest = new DeleteAllSubtaskRequest();
    UpdateSubtaskRequest updateSubtaskRequest = new UpdateSubtaskRequest();
    GetEpicSubtasksRequest getEpicSubtasksRequest = new GetEpicSubtasksRequest();
    GetHistoryRequest getHistoryRequest = new GetHistoryRequest();
    GetPrioritizedTasksRequest getPrioritizedTasksRequest = new GetPrioritizedTasksRequest();

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
    public void getEpicTestNegative() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");

        createEpicRequest.createEpic(epic);

        int statusCode = getEpicRequest.getEpicByIdNegative(2);

        assertThat(statusCode).isEqualTo(404);
    }

    @Test
    public void getEpicTestPositive() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");

        createEpicRequest.createEpic(epic);

        GetEpicResponse getEpicResponse = getEpicRequest.getEpicByIdPositive(1);

        assertThat(getEpicResponse.getSubtasks()).isEqualTo(List.of());
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

    @Test
    public void createSubtaskTest() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");
        CreateSubtaskDataSet subtask = new CreateSubtaskDataSet("EXE1", "payment1",
                "1691684240", 40);

        createEpicRequest.createEpic(epic);

        CreateSubtaskResponse response = createSubtaskRequest.createSubtask(subtask, 1);

        assertThat(response.getEpicId()).isEqualTo(1);
        assertThat(response.getId()).isEqualTo(2);
        assertThat(response.getTitle()).isEqualTo(subtask.getTitle());
        assertThat(response.getDescription()).isEqualTo(subtask.getDescription());
        assertThat(response.getStartTime()).isEqualTo(Integer.parseInt(subtask.getStartTime()));
        assertThat(response.getDuration()).isEqualTo(subtask.getDuration());
        assertThat(response.getEndTime()).isEqualTo(
                Integer.parseInt(subtask.getStartTime()) + subtask.getDuration() * 60 * 1000);
    }

    @Test
    public void getSubtaskTestPositive() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");
        CreateSubtaskDataSet subtask = new CreateSubtaskDataSet("EXE1", "payment1",
                "1691684240", 40);

        createEpicRequest.createEpic(epic);
        createSubtaskRequest.createSubtask(subtask, 1);

        GetSubtaskResponse getSubtaskResponse = getSubtaskRequest.getSubtaskByIdPositive(2);

        assertThat(getSubtaskResponse.getEpicId()).isEqualTo(1);
        assertThat(getSubtaskResponse.getId()).isEqualTo(2);
        assertThat(getSubtaskResponse.getTitle()).isEqualTo(subtask.getTitle());
        assertThat(getSubtaskResponse.getDescription()).isEqualTo(subtask.getDescription());
        assertThat(getSubtaskResponse.getStartTime()).isEqualTo(Integer.parseInt(subtask.getStartTime()));
        assertThat(getSubtaskResponse.getDuration()).isEqualTo(subtask.getDuration());
        assertThat(getSubtaskResponse.getEndTime()).isEqualTo(
                Integer.parseInt(subtask.getStartTime()) + subtask.getDuration() * 60 * 1000);
    }

    @Test
    public void getSubtaskTestNegative() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");
        CreateSubtaskDataSet subtask = new CreateSubtaskDataSet("EXE1", "payment1",
                "1691684240", 40);

        createEpicRequest.createEpic(epic);
        createSubtaskRequest.createSubtask(subtask, 1);

        int statusCode = getSubtaskRequest.getSubtaskByIdNegative(3);

        assertThat(statusCode).isEqualTo(404);
    }

    @Test
    public void deleteSubtaskTestPositive() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");
        CreateSubtaskDataSet subtask = new CreateSubtaskDataSet("EXE1", "payment1",
                "1691684240", 40);

        createEpicRequest.createEpic(epic);
        createSubtaskRequest.createSubtask(subtask, 1);

        int deleteStatusCode = deleteSubtaskRequest.deleteSubtaskById(2);

        assertThat(deleteStatusCode).isEqualTo(200);
    }

    @Test
    public void deleteSubtaskTestNegative() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");
        CreateSubtaskDataSet subtask = new CreateSubtaskDataSet("EXE1", "payment1",
                "1691684240", 40);

        createEpicRequest.createEpic(epic);
        createSubtaskRequest.createSubtask(subtask, 1);

        int deleteStatusCode = deleteSubtaskRequest.deleteSubtaskById(3);

        assertThat(deleteStatusCode).isEqualTo(404);
    }

    @Test
    public void deleteAllSubtasks() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");
        CreateSubtaskDataSet subtask = new CreateSubtaskDataSet("EXE1", "payment1",
                "1691684240", 40);

        createEpicRequest.createEpic(epic);
        createSubtaskRequest.createSubtask(subtask, 1);

        int deleteStatusCode = deleteAllSubtaskRequest.deleteAllSubtasks();

        assertThat(deleteStatusCode).isEqualTo(200);
    }

    @Test
    public void updateSubtaskTestPositive() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");
        CreateSubtaskDataSet subtask = new CreateSubtaskDataSet("EXE1", "payment1",
                "1691684240", 40);
        UpdateSubtaskDataSet updateSubtask = new UpdateSubtaskDataSet(2, "DONE", "EXE1",
                "payment1", "1691684240", 40);

        createEpicRequest.createEpic(epic);
        createSubtaskRequest.createSubtask(subtask, 1);

        UpdateSubtaskResponse updateSubtaskResponse = updateSubtaskRequest.updateSubtaskPositive(updateSubtask);

        assertThat(updateSubtaskResponse.getEpicId()).isEqualTo(1);
        assertThat(updateSubtaskResponse.getId()).isEqualTo(2);
        assertThat(updateSubtaskResponse.getTitle()).isEqualTo(updateSubtask.getTitle());
        assertThat(updateSubtaskResponse.getDescription()).isEqualTo(updateSubtask.getDescription());
        assertThat(updateSubtaskResponse.getStartTime()).isEqualTo(Integer.parseInt(updateSubtask.getStartTime()));
        assertThat(updateSubtaskResponse.getDuration()).isEqualTo(updateSubtask.getDuration());
        assertThat(updateSubtaskResponse.getEndTime()).isEqualTo(
                Integer.parseInt(updateSubtask.getStartTime()) + updateSubtask.getDuration() * 60 * 1000);
    }

    @Test
    public void updateSubtaskTestNegative() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");
        CreateSubtaskDataSet subtask = new CreateSubtaskDataSet("EXE1", "payment1",
                "1691684240", 40);
        UpdateSubtaskDataSet updateSubtask = new UpdateSubtaskDataSet(7, "DONE", "EXE1",
                "payment1", "1691684240", 40);

        createEpicRequest.createEpic(epic);
        createSubtaskRequest.createSubtask(subtask, 1);

        int statusCode = updateSubtaskRequest.updateSubtaskNegative(updateSubtask);

        assertThat(statusCode).isEqualTo(400);
    }

    @Test
    public void getEpicSubtasks() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");
        CreateSubtaskDataSet subtask = new CreateSubtaskDataSet("EXE1", "payment1",
                "1691684240", 40);

        createEpicRequest.createEpic(epic);
        createSubtaskRequest.createSubtask(subtask, 1);

        List<GetSubtaskResponse> response = getEpicSubtasksRequest.getEpicSubtasksRequestPositive(1);

        assertThat(response.get(0)).isEqualTo(getSubtaskRequest.getSubtaskByIdPositive(2));
    }

    @Test
    public void GetHistoryTest() {
        CreateTaskDataSet task = new CreateTaskDataSet("EXE1", "payment1",
                "1691684240", 40);

        createTaskRequest.createTask(task);

        GetTaskResponse getTaskResponse = getTaskRequest.getTaskByIdPositive(1);
        List<GetTaskResponse> history = getHistoryRequest.getHistoryRequest();

        assertThat(history).isEqualTo(List.of(getTaskResponse));
    }

    @Test
    public void GetPrioritizedTasksTest() {
        CreateTaskDataSet task1 = new CreateTaskDataSet("EXE1", "payment1",
                "1691684240", 40);
        CreateTaskDataSet task2 = new CreateTaskDataSet("EXE1", "payment1",
                "1681684240", 40);

        createTaskRequest.createTask(task1);
        createTaskRequest.createTask(task2);

        GetTaskResponse getTask1Response = getTaskRequest.getTaskByIdPositive(1);
        GetTaskResponse getTask2Response = getTaskRequest.getTaskByIdPositive(2);

        List<GetTaskResponse> prioritizedTasks = getPrioritizedTasksRequest.getPrioritizedTasks();
        System.out.println(prioritizedTasks);
        assertThat(prioritizedTasks).isEqualTo(List.of(getTask2Response, getTask1Response));
    }
    @AfterEach
    public void stopServer() {
        kvServer.stop();
        httpTaskServer.stop();
    }
}
