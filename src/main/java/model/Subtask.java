package model;

import model.constants.TaskStatus;

public class Subtask extends Task {
	private final Epic epic;

	public Subtask(String title, String description, Epic epic, TaskStatus status) {
		super(title, description, status);
		this.epic = epic;
	}

	public Subtask(int id, String title, String description, Epic epic, TaskStatus status) {
		super(id, title, description, status);
		this.epic = epic;
	}

	public Epic getEpic() {
		return epic;
	}
	@Override
	public String toString() {
		return "SubTask{" +
				"id=" + getId() +
				", title='" + getTitle() + '\'' +
				", status=" + getStatus() +
				", description='" + getDescription() + '\'' +
				", epicId=" + epic.getId() +
				'}';
	}
}
