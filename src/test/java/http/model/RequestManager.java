package http.model;

import http.model.epic.create.CreateEpicRequest;
import http.model.epic.delete.DeleteEpicRequest;
import http.model.epic.deleteall.DeleteAllEpicRequest;
import http.model.epic.get.GetEpicRequest;
import http.model.epic.update.UpdateEpicRequest;
import http.model.history.GetHistoryRequest;
import http.model.subtask.create.CreateSubtaskRequest;
import http.model.subtask.delete.DeleteSubtaskRequest;
import http.model.subtask.deleteall.DeleteAllSubtaskRequest;
import http.model.subtask.gelepicsubtask.GetEpicSubtasksRequest;
import http.model.subtask.get.GetSubtaskRequest;
import http.model.subtask.update.UpdateSubtaskRequest;
import http.model.task.create.CreateTaskRequest;
import http.model.task.delete.DeleteTaskRequest;
import http.model.task.deleteall.DeleteAllTasksRequest;
import http.model.task.get.GetTaskRequest;
import http.model.task.prioritized.GetPrioritizedTasksRequest;
import http.model.task.update.UpdateTaskRequest;

public class RequestManager {
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

    public CreateTaskRequest createTaskRequest() {
        return createTaskRequest;
    }

    public GetTaskRequest getTaskRequest() {
        return getTaskRequest;
    }

    public DeleteAllTasksRequest deleteAllTasksRequest() {
        return deleteAllTasksRequest;
    }


    public DeleteTaskRequest deleteTaskRequest() {
        return deleteTaskRequest;
    }

    public UpdateTaskRequest updateTaskRequest() {
        return updateTaskRequest;
    }

    public CreateEpicRequest createEpicRequest() {
        return createEpicRequest;
    }

    public GetEpicRequest getEpicRequest() {
        return getEpicRequest;
    }

    public DeleteEpicRequest deleteEpicRequest() {
        return deleteEpicRequest;
    }

    public DeleteAllEpicRequest deleteAllEpicRequest() {
        return deleteAllEpicRequest;
    }

    public UpdateEpicRequest updateEpicRequest() {
        return updateEpicRequest;
    }

    public CreateSubtaskRequest createSubtaskRequest() {
        return createSubtaskRequest;
    }

    public GetSubtaskRequest getSubtaskRequest() {
        return getSubtaskRequest;
    }

    public DeleteSubtaskRequest deleteSubtaskRequest() {
        return deleteSubtaskRequest;
    }

    public DeleteAllSubtaskRequest deleteAllSubtaskRequest() {
        return deleteAllSubtaskRequest;
    }

    public UpdateSubtaskRequest updateSubtaskRequest() {
        return updateSubtaskRequest;
    }

    public GetEpicSubtasksRequest getEpicSubtasksRequest() {
        return getEpicSubtasksRequest;
    }

    public GetHistoryRequest getHistoryRequest() {
        return getHistoryRequest;
    }

    public GetPrioritizedTasksRequest getPrioritizedTasksRequest() {
        return getPrioritizedTasksRequest;
    }
}
