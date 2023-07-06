package model;

import model.constants.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
	private List<Integer> subtasks = new ArrayList<>();

	public Epic(String title, String description) {
		super(title, description);
	}

	public Epic(int id, String title, String description, TaskStatus status) {
		super(id, title, description, status);
	}

	public void addSubtask(Integer subtask) {
		subtasks.add(subtask);
	}

	public void removeSubtask(int id) {
		subtasks.removeIf(s -> s == id);
	}

	public List<Integer> getSubtasks() {
		return subtasks;
	}

	public void setSubtasks(List<Integer> subtasks) {
		this.subtasks = subtasks;
	}

	@Override
	public String toString() {
		return "Epic{" +
				"id=" + getId() +
				", name='" + getTitle() + '\'' +
				", status=" + getStatus() +
				", description='" + getDescription() + '\'' +
				'}';
	}
}
