package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.constants.TaskStatuses;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Manager {
	private final Map<Integer, Task> tasks = new HashMap<>();
	private final Map<Integer, Epic> epics = new HashMap<>();
	private int i = 0;

	public List<Task> getAllTasks() {
		return new ArrayList<>(tasks.values());
	}

	public List<Epic> getAllEpics() {
		return new ArrayList<>(epics.values());
	}

	public List<Subtask> getAllSubtasks() {
		return epics.values()
				.stream()
				.map(Epic::getSubtasks)
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}

	public void removeAllTasks() {
		tasks.clear();
	}

	public void removeAllEpics() {
		epics.clear();
	}

	public void removeAllSubtasks() {
		epics.values().stream()
				.map(Epic::getSubtasks)
				.forEach(List::clear);
	}

	public Task getTaskById(int id) {
		return  tasks.get(id);
	}

	public Epic getEpicById(int id) {
		return  epics.get(id);
	}

	public Subtask getSubtaskById(int id) {
		return epics.values()
				.stream()
				.map(Epic::getSubtasks)
				.flatMap(List::stream)
				.filter(s -> s.getId() == id)
				.findFirst()
				.orElse(null);
	}

	public List<Subtask> getAllSubtasksFromEpic(Epic epic) {
		return epics.get(epic.getId()).getSubtasks();
	}

	public void createTask(Task task) {
		task.setId(getNewId());
		tasks.put(task.getId(), task);
	}

	public void createEpic(Epic epic) {
		epic.setId(getNewId());
		epics.put(epic.getId(), epic);
	}

	public void createSubtask(Subtask subtask, Epic epic) {
		subtask.setId(getNewId());

		epic.addSubtask(subtask);
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

	public void updateSubtask(int subtaskId, Subtask newSubtaskData) {
		newSubtaskData.setId(subtaskId);

		int epicId = getEpicBySubtaskId(subtaskId).getId();

		epics.get(epicId).removeSubtask(subtaskId);
		epics.get(epicId).getSubtasks().add(newSubtaskData);

		checkEpicStatus(getEpicBySubtaskId(subtaskId));
	}

	public void removeTaskById(int id) {
		tasks.remove(id);
	}

	public void removeEpicById(int id) {
		epics.remove(id);
	}

	public void removeSubtaskById(int id) {
		epics.values().stream()
				.filter(s -> s.getSubtasks().contains(getSubtaskById(id)))
				.forEach(e -> e.removeSubtask(id));
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

	private Epic getEpicBySubtaskId(int subtaskId) {
		return epics.values().stream()
				.filter(epic -> epic.getSubtasks().contains(getSubtaskById(subtaskId)))
				.findFirst()
				.orElse(null);
	}

	private int getNewId() {
		return ++i;
	}
}
