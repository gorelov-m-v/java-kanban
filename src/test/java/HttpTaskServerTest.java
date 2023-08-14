import http.model.RequestManager;
import http.model.epic.create.CreateEpicDataSet;
import http.model.epic.create.CreateEpicResponse;
import http.model.epic.get.GetEpicResponse;
import http.model.epic.update.UpdateEpicDataSet;
import http.model.epic.update.UpdateEpicResponse;
import http.server.HttpTaskServer;
import http.server.KVServer;
import http.model.subtask.create.CreateSubtaskDataSet;
import http.model.subtask.create.CreateSubtaskResponse;
import http.model.subtask.get.GetSubtaskResponse;
import http.model.subtask.update.UpdateSubtaskDataSet;
import http.model.subtask.update.UpdateSubtaskResponse;
import http.model.task.create.CreateTaskDataSet;
import http.model.task.create.CreateTaskResponse;
import http.model.task.get.GetTaskResponse;
import http.model.task.update.UpdateTaskDataSet;
import http.model.task.update.UpdateTaskResponse;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class HttpTaskServerTest {
    private static KVServer kvServer;
    private static HttpTaskServer httpTaskServer;
    RequestManager requests = new RequestManager();

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

        CreateTaskResponse response = requests.createTaskRequest().createTask(task);

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

        requests.createTaskRequest().createTask(task);

        GetTaskResponse response = requests.getTaskRequest().getTaskByIdPositive(1);

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

        requests.createTaskRequest().createTask(task);

        int statusCode = requests.getTaskRequest().getTaskByIdNegative(2);

        assertThat(statusCode).isEqualTo(404);
    }

    @Test
    public void deleteTaskTestPositive() {
        CreateTaskDataSet task = new CreateTaskDataSet("EXE1", "payment1",
                "1691684240", 40);

        requests.createTaskRequest().createTask(task);

        int deleteStatusCode = requests.deleteTaskRequest().deleteTaskById(1);
        int getStatusCode = requests.getTaskRequest().getTaskByIdNegative(1);

        assertThat(deleteStatusCode).isEqualTo(200);
        assertThat(getStatusCode).isEqualTo(404);
    }

    @Test
    public void deleteTaskTestNegative() {
        CreateTaskDataSet task = new CreateTaskDataSet("EXE1", "payment1",
                "1691684240", 40);

        requests.createTaskRequest().createTask(task);

        int deleteStatusCode = requests.deleteTaskRequest().deleteTaskById(2);

        assertThat(deleteStatusCode).isEqualTo(404);
    }

    @Test
    public void UpdateTestPositive() {
        CreateTaskDataSet task = new CreateTaskDataSet("EXE1", "payment1",
                "1691684240", 40);
        UpdateTaskDataSet updateTaskDataSet = new UpdateTaskDataSet(1, "EXE2", "payment2",
                "DONE", "1691704459818", 40);

        requests.createTaskRequest().createTask(task);
        UpdateTaskResponse updateTaskResponse =  requests.updateTaskRequest().updateTaskPositive(updateTaskDataSet);

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

        requests.createTaskRequest().createTask(task);
        int statusCode =  requests.updateTaskRequest().updateTaskNegative(updateTaskDataSet);

        assertThat(statusCode).isEqualTo(404);
    }

    @Test
    public void deleteAllTasks() {
        CreateTaskDataSet task = new CreateTaskDataSet("EXE1", "payment1",
                "1691684240", 40);

        requests.createTaskRequest().createTask(task);

        int deleteStatusCode = requests.deleteAllTasksRequest().deleteAllTasks();
        int getStatusCode = requests.getTaskRequest().getTaskByIdNegative(1);

        assertThat(deleteStatusCode).isEqualTo(200);
        assertThat(getStatusCode).isEqualTo(404);
    }

    @Test
    public void createEpicTest() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");

        CreateEpicResponse response = requests.createEpicRequest().createEpic(epic);

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

        requests.createEpicRequest().createEpic(epic);

        int statusCode = requests.getEpicRequest().getEpicByIdNegative(2);

        assertThat(statusCode).isEqualTo(404);
    }

    @Test
    public void getEpicTestPositive() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");

        requests.createEpicRequest().createEpic(epic);

        GetEpicResponse getEpicResponse = requests.getEpicRequest().getEpicByIdPositive(1);

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

        requests.createEpicRequest().createEpic(epic);

        int deleteStatusCode = requests.deleteEpicRequest().deleteEpicById(1);
        int getStatusCode = requests.getEpicRequest().getEpicByIdNegative(1);

        assertThat(deleteStatusCode).isEqualTo(200);
        assertThat(getStatusCode).isEqualTo(404);
    }

    @Test
    public void deleteEpicTestNegative() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");

        requests.createEpicRequest().createEpic(epic);

        int deleteStatusCode = requests.deleteEpicRequest().deleteEpicById(2);

        assertThat(deleteStatusCode).isEqualTo(404);
    }

    @Test
    public void deleteAllEpics() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");

        requests.createEpicRequest().createEpic(epic);

        int deleteStatusCode = requests.deleteAllEpicRequest().deleteAllEpics();

        assertThat(deleteStatusCode).isEqualTo(200);
    }

    @Test
    public void UpdateEpicPositive() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");
        UpdateEpicDataSet updateEpicDataSet = new UpdateEpicDataSet(1, "EXE2", "payment2");

        requests.createEpicRequest().createEpic(epic);
        UpdateEpicResponse updateEpicResponse =  requests.updateEpicRequest().updateEpicPositive(updateEpicDataSet);

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

        requests.createEpicRequest().createEpic(epic);
        int statusCode =  requests.updateEpicRequest().updateEpicNegative(updateEpicDataSet);

        assertThat(statusCode).isEqualTo(404);
    }

    @Test
    public void createSubtaskTest() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");
        CreateSubtaskDataSet subtask = new CreateSubtaskDataSet("EXE1", "payment1",
                "1691684240", 40);

        requests.createEpicRequest().createEpic(epic);

        CreateSubtaskResponse response = requests.createSubtaskRequest().createSubtask(subtask, 1);

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

        requests.createEpicRequest().createEpic(epic);
        requests.createSubtaskRequest().createSubtask(subtask, 1);

        GetSubtaskResponse getSubtaskResponse = requests.getSubtaskRequest().getSubtaskByIdPositive(2);

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

        requests.createEpicRequest().createEpic(epic);
        requests.createSubtaskRequest().createSubtask(subtask, 1);

        int statusCode = requests.getSubtaskRequest().getSubtaskByIdNegative(3);

        assertThat(statusCode).isEqualTo(404);
    }

    @Test
    public void deleteSubtaskTestPositive() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");
        CreateSubtaskDataSet subtask = new CreateSubtaskDataSet("EXE1", "payment1",
                "1691684240", 40);

        requests.createEpicRequest().createEpic(epic);
        requests.createSubtaskRequest().createSubtask(subtask, 1);

        int deleteStatusCode = requests.deleteSubtaskRequest().deleteSubtaskById(2);

        assertThat(deleteStatusCode).isEqualTo(200);
    }

    @Test
    public void deleteSubtaskTestNegative() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");
        CreateSubtaskDataSet subtask = new CreateSubtaskDataSet("EXE1", "payment1",
                "1691684240", 40);

        requests.createEpicRequest().createEpic(epic);
        requests.createSubtaskRequest().createSubtask(subtask, 1);

        int deleteStatusCode = requests.deleteSubtaskRequest().deleteSubtaskById(3);

        assertThat(deleteStatusCode).isEqualTo(404);
    }

    @Test
    public void deleteAllSubtasks() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");
        CreateSubtaskDataSet subtask = new CreateSubtaskDataSet("EXE1", "payment1",
                "1691684240", 40);

        requests.createEpicRequest().createEpic(epic);
        requests.createSubtaskRequest().createSubtask(subtask, 1);

        int deleteStatusCode = requests.deleteAllSubtaskRequest().deleteAllSubtasks();

        assertThat(deleteStatusCode).isEqualTo(200);
    }

    @Test
    public void updateSubtaskTestPositive() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");
        CreateSubtaskDataSet subtask = new CreateSubtaskDataSet("EXE1", "payment1",
                "1691684240", 40);
        UpdateSubtaskDataSet updateSubtask = new UpdateSubtaskDataSet(2, "DONE", "EXE1",
                "payment1", "1691684240", 40);

        requests.createEpicRequest().createEpic(epic);
        requests.createSubtaskRequest().createSubtask(subtask, 1);

        UpdateSubtaskResponse updateSubtaskResponse = requests.updateSubtaskRequest()
                .updateSubtaskPositive(updateSubtask);

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

        requests.createEpicRequest().createEpic(epic);
        requests.createSubtaskRequest().createSubtask(subtask, 1);

        int statusCode = requests.updateSubtaskRequest().updateSubtaskNegative(updateSubtask);

        assertThat(statusCode).isEqualTo(400);
    }

    @Test
    public void getEpicSubtasks() {
        CreateEpicDataSet epic = new CreateEpicDataSet("EXE1", "payment1");
        CreateSubtaskDataSet subtask = new CreateSubtaskDataSet("EXE1", "payment1",
                "1691684240", 40);

        requests.createEpicRequest().createEpic(epic);
        requests.createSubtaskRequest().createSubtask(subtask, 1);

        List<GetSubtaskResponse> response = requests.getEpicSubtasksRequest().getEpicSubtasksRequestPositive(1);

        assertThat(response.get(0)).isEqualTo(requests.getSubtaskRequest().getSubtaskByIdPositive(2));
    }

    @Test
    public void GetHistoryTest() {
        CreateTaskDataSet task = new CreateTaskDataSet("EXE1", "payment1",
                "1691684240", 40);

        requests.createTaskRequest().createTask(task);

        GetTaskResponse getTaskResponse = requests.getTaskRequest().getTaskByIdPositive(1);
        List<GetTaskResponse> history = requests.getHistoryRequest().getHistoryRequest();

        assertThat(history).isEqualTo(List.of(getTaskResponse));
    }

    @Test
    public void GetPrioritizedTasksTest() {
        CreateTaskDataSet task1 = new CreateTaskDataSet("EXE1", "payment1",
                "1691684240", 40);
        CreateTaskDataSet task2 = new CreateTaskDataSet("EXE1", "payment1",
                "1681684240", 40);

        requests.createTaskRequest().createTask(task1);
        requests.createTaskRequest().createTask(task2);

        GetTaskResponse getTask1Response = requests.getTaskRequest().getTaskByIdPositive(1);
        GetTaskResponse getTask2Response = requests.getTaskRequest().getTaskByIdPositive(2);

        List<GetTaskResponse> prioritizedTasks = requests.getPrioritizedTasksRequest().getPrioritizedTasks();
        System.out.println(prioritizedTasks);
        assertThat(prioritizedTasks).isEqualTo(List.of(getTask2Response, getTask1Response));
    }
    @AfterEach
    public void stopServer() {
        kvServer.stop();
        httpTaskServer.stop();
    }
}
