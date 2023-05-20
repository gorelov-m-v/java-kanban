package model;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Manager {
	private final HashMap<Integer, Task> tasks = new HashMap<>();
	private final HashMap<Integer, Epic> epics = new HashMap<>();
	private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
	private int i = 0;

	private int getNewId() {
		return ++i;
	}
	// Получение списка всех задач.
	public List<Task> getAllTasks() {
		return tasks.values().stream().collect(Collectors.toList());
	}

	public List<Epic> getAllEpics() {
		return epics.values().stream().collect(Collectors.toList());
	}

	public List<Subtask> getAllSubtasks() {
		return subtasks.values().stream().collect(Collectors.toList());
	}
	// Удаление всех задач.
	public void removeAllTasks() {
		tasks.clear();
	}

	public void removeAllEpics() {
		epics.clear();
	}

	public void removeAllSubtasks() {
		subtasks.clear();
	}
	// Получение по идентификатору.
	public Task getTaskById(int id) {
		return  tasks.get(id);
	}

	public Epic getEpicById(int id) {
		return  epics.get(id);
	}

	public Subtask getSubtaskById(int id) {
		return  subtasks.get(id);
	}
	// Создание. Сам объект должен передаваться в качестве параметра.
	public void createTask(Task task) {
		task.setId(getNewId());
		tasks.put(task.getId(), task);
	}

	public void createEpic(Epic epic) {
		epic.setId(getNewId());
		epics.put(epic.getId(), epic);
	}

	public void createSubtask(Subtask subtask) {
		subtask.setId(getNewId());
		subtasks.put(subtask.getId(), subtask);
	}

	// Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
	public void updateTask(Task task) {
		tasks.put(task.getId(), task);
	}

	public void updateEpic(Epic epic) {
		epics.put(epic.getId(), epic);
	}

	public void updateSubtask(Subtask subtask) {
		subtasks.put(subtask.getId(), subtask);
	}

	// Удаление по идентификатору.
	public void removeTaskById(int id) {
		tasks.remove(id);
	}

	public void removeEpicById(int id) {
		epics.remove(id);
	}

	public void removeSubtaskById(int id) {
		subtasks.remove(id);
	}

	// Получение списка всех подзадач определённого эпика.
	public List<Subtask> getAllSubtasksFromEpic(int epicId) {
		return epics.get(epicId).getSubtasks();
	}

	// Управление статусами.
	public void checkEpicStatus(Epic epic) {
		List<String> subtaskStatuses = epic.getSubtasks()
				.stream()
				.map(Subtask::getStatus)
				.collect(Collectors.toList());

		boolean statusNew = subtaskStatuses.stream().allMatch(s -> s.equals("NEW"));
		boolean statusDone = subtaskStatuses.stream().allMatch(s -> s.equals("DONE"));

		if (statusNew) {
			epic.setStatus("NEW");
		} else if (statusDone) {
			epic.setStatus("DONE");
		} else {
			epic.setStatus("IN_PROGRESS");
		}
	}
}
