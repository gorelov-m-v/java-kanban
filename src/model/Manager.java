package model;

import model.constants.TaskStatuses;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Manager {
	private final HashMap<Integer, Task> tasks = new HashMap<>();
	private final HashMap<Integer, Epic> epics = new HashMap<>();
	private int i = 0;

	private int getNewId() {
		return ++i;
	}

	public List<Task> getAllTasks() {
		return tasks.values().stream().collect(Collectors.toList());
	}

	public List<Epic> getAllEpics() {
		return epics.values().stream().collect(Collectors.toList());
	}

	public List<Subtask> getAllSubtasks() {
		List<Subtask> allSubTasks = new ArrayList<>();
		for (Epic epic : epics.values()) {
			allSubTasks = Stream.concat(allSubTasks.stream(), epic.getSubtasks().stream())
					.collect(Collectors.toList());
		}
		return allSubTasks;
	}

	public void removeAllTasks() {
		tasks.clear();
	}

	public void removeAllEpics() {
		epics.clear();
	}

	public void removeAllSubtasks() {
		for (Epic epic : epics.values()) {
			epic.getSubtasks().clear();
		}
	}

	public Task getTaskById(int id) {
		return  tasks.get(id);
	}

	public Epic getEpicById(int id) {
		return  epics.get(id);
	}

	public Subtask getSubtaskById(int id) {
		Subtask subtask = null;
		for (Epic epic : epics.values()) {
			for (Subtask sub : epic.getSubtasks()) {
				if (sub.getId() == id) {
					subtask = sub;
				}
			}
		}
		return subtask;
	}

	private Epic getEpicBySubtaskId(int subtaskId) {
		Epic returnedEpic = null;
		for (Epic epic : epics.values()) {
			for ( Subtask subtask : epic.getSubtasks()) {
				if (subtask.getId() == subtaskId) {
					returnedEpic = epic;
					break;
				}
			}
		}
		return returnedEpic;
	}

	public void createTask(Task task) {
		task.setId(getNewId());
		tasks.put(task.getId(), task);
	}

	public void createEpic(Epic epic) {
		epic.setId(getNewId());
		epics.put(epic.getId(), epic);
	}

	public void createSubtask(Subtask subtaskData, Epic epic) {
		epic.addSubtask(new Subtask(getNewId(),
				subtaskData.getTitle(),
				subtaskData.getDescription(),
				epic,
				subtaskData.getStatus()));
	}

	public void updateTask(Task task, String title, String description, TaskStatuses status) {
		task.setTitle(title);
		task.setDescription(description);
		task.setStatus(status);
		tasks.put(task.getId(), task);
	}

	public void updateEpic(Epic epic, String title, String description) {
		epic.setTitle(title);
		epic.setDescription(description);
		epics.put(epic.getId(), epic);
	}

	private int getIndexBySubtaskId(int subtaskId) {
		int index = Integer.MIN_VALUE;
		Epic epic = getEpicBySubtaskId(subtaskId);
		for (int i = 0; i < epic.getSubtasks().size(); i++) {
			if (epic.getSubtasks().get(i).getId() == subtaskId)
				index = i;
		}
		return index;
	}

	public void updateSubtask(int subtaskId, Subtask newSubtaskData) {
		Subtask updatedSubtask = getSubtaskById(subtaskId);
		updatedSubtask.setStatus(newSubtaskData.getStatus());
		updatedSubtask.setTitle(newSubtaskData.getTitle());
		updatedSubtask.setDescription(newSubtaskData.getDescription());

		int epicId = getEpicBySubtaskId(subtaskId).getId();

		epics.get(epicId).getSubtasks().remove(getIndexBySubtaskId(subtaskId));
		epics.get(epicId).getSubtasks().add(updatedSubtask);

		checkEpicStatus(getEpicBySubtaskId(subtaskId));
	}

	public void removeTaskById(int id) {
		tasks.remove(id);
	}

	public void removeEpicById(int id) {
		epics.remove(id);
	}

	public void removeSubtaskById(int id) {
		int epicId = getEpicBySubtaskId(id).getId();
		epics.get(epicId).getSubtasks().remove(getIndexBySubtaskId(id));
	}

	public List<Subtask> getAllSubtasksFromEpic(Epic epic) {
		return epics.get(epic.getId()).getSubtasks();
	}

	private void checkEpicStatus(Epic epic) {
		List<TaskStatuses> subtaskStatuses = epic.getSubtasks()
				.stream()
				.map(Subtask::getStatus)
				.collect(Collectors.toList());

		boolean statusNew = subtaskStatuses.stream().allMatch(s -> s.equals(TaskStatuses.NEW));
		boolean statusDone = subtaskStatuses.stream().allMatch(s -> s.equals(TaskStatuses.DONE));

		if (statusNew) {
			epic.setStatus(TaskStatuses.NEW);
		} else if (statusDone) {
			epic.setStatus(TaskStatuses.DONE);
		} else {
			epic.setStatus(TaskStatuses.IN_PROGRESS);
		}
	}
}
