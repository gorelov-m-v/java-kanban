package model;

import java.io.FilterOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Manager {
	private final HashMap<Integer, Task> tasks = new HashMap<>();
	private final HashMap<Integer, Epic> epics = new HashMap<>();

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

	public List<Subtask> getAllSubtasks(Epic epic) {
		return epic.getSubtasks();
	}
	// Удаление всех задач.
	public void removeAllTasks() {
		tasks.clear();
	}

	public void removeAllEpics() {
		epics.clear();
	}

	public void removeAllSubtasks(Epic epic) {
		epic.getSubtasks().clear();
	}
	// Получение по идентификатору.
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

	public Epic getEpicBySubtaskId(int subtaskId) {
		Epic returnedEpic = null;
		for (Epic epic : epics.values()) {
			for ( Subtask subtask : epic.getSubtasks()) {
				if (subtask.getId() == subtaskId) {
					returnedEpic = epic;
				}
			}
		}
		return returnedEpic;
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

	public void createSubtask(Subtask subtaskData, Epic epic) {
		Subtask newSubtask = new Subtask(getNewId(), subtaskData.getTitle(), subtaskData.getDescription(), subtaskData.getStatus());
		epic.addSubtask(newSubtask);
	}

	// Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
	public void updateTask(Task task) {
		tasks.put(task.getId(), task);
	}

	public void updateEpic(Epic epic) {
		epics.put(epic.getId(), epic);
	}

	public int getIndexBySubtaskId(int subtaskId) {
		int index = -1;
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





	// Удаление по идентификатору.
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

	// Получение списка всех подзадач определённого эпика.
	public List<Subtask> getAllSubtasksFromEpic(Epic epic) {
		return epics.get(epic.getId()).getSubtasks();
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
