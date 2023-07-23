package model;

import model.constant.TaskStatus;
import java.time.Instant;

public class Subtask extends Task {
	private final int epicId;

	public Subtask(String title, String description, int epicId, Instant startTime, long duration) {
		super(title, description, startTime, duration);
		this.epicId = epicId;
	}

	public Subtask(String title, String description, int epicId, Instant startTime, long duration, TaskStatus status) {
		super(title, description, startTime, duration, status);
		this.epicId = epicId;
	}

	public Subtask(int id, String title, String description, int epicId, TaskStatus status) {
		super(id, title, description, status);
		this.epicId = epicId;
	}

	public Subtask(int id, String title, String description, int epicId, TaskStatus status, Instant startTime, long duration) {
		super(id, title, description, status, startTime, duration);
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
				", startTime='" + getStartTime() + '\'' +
				", duration='" + getDuration() + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		Subtask subtask = (Subtask) o;

		return epicId == subtask.epicId;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + epicId;
		return result;
	}
}
