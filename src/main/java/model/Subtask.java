package model;

import model.constant.TaskStatus;

public class Subtask extends Task {
	private final int epicId;

	public Subtask(String title, String description, int epicId, TaskStatus status) {
		super(title, description, status);
		this.epicId = epicId;
	}

	public Subtask(int id, String title, String description, int epicId, TaskStatus status) {
		super(id, title, description, status);
		this.epicId = epicId;
	}

	public Subtask(String title, String description, int epicId) {
		super(title, description);
		this.epicId = epicId;
	}

	public int getEpicId() {
		return epicId;
	}
	@Override
	public String toString() {
		return "SubTask{" +
				"id=" + getId() +
				", title='" + getTitle() + '\'' +
				", status=" + getStatus() +
				", description='" + getDescription() + '\'' +
				", epicId=" + epicId +
				'}';
	}
}
