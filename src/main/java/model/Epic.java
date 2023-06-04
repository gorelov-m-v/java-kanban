package model;


import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
	private List<Subtask> subtasks = new ArrayList<>();

	public Epic(String title, String description) {
		super(title, description);
	}

	public void addSubtask(Subtask subtask) {
		subtasks.add(subtask);
	}

	public void removeSubtask(int id) {
		subtasks.removeIf(s -> s.getId() == id);
	}

	public List<Subtask> getSubtasks() {
		return subtasks;
	}

	public void setSubtasks(List<Subtask> subtasks) {
		this.subtasks = subtasks;
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
