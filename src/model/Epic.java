package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
	private List<Subtask> subtasks = new ArrayList<>();

	public Epic(String title, String description) {
		super(title, description);
	}

	public List<Subtask> getSubtasks() {
		return subtasks;
	}

	public void addSubtask(Subtask subtask) {
		subtasks.add(subtask);
	}

	@Override
	public String toString() {
		return "Epic{" +
				"id=" + getId() +
				", name='" + getTitle() + '\'' +
				", status=" + getStatus() +
				", description='" + getDescription() + '\'' +
				", subTasksCount=" + getSubtasks().size() +
				'}';
	}
}
