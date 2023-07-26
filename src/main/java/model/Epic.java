package model;

import model.constant.TaskStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Epic extends Task {
	private List<Integer> subtasks = new ArrayList<>();

	public Epic(String title, String description) {
		super(title, description);
	}

	public Epic(int id, String title, String description, TaskStatus status) {
		super(id, title, description, status);
	}

	public Epic(int id, String title, String description, TaskStatus status, Instant startTime, long duration) {
		super(id, title, description, status, startTime, duration);
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
				", subtaskCount=" + getSubtasks().size() +
				", description='" + getDescription() + '\'' +
				", startTime='" + getStartTime() + '\'' +
				", endTime='" + getEndTime() + '\'' +
				", duration='" + getDuration() + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		Epic epic = (Epic) o;

		return Objects.equals(subtasks, epic.subtasks);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (subtasks != null ? subtasks.hashCode() : 0);
		return result;
	}
}
