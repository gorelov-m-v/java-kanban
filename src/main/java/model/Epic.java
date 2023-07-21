package model;

import model.constant.TaskStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class Epic extends Task {
	private List<Integer> subtasks = new ArrayList<>();
	private Instant endTime;

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
	public Instant getEndTime() {
		return endTime;
	}

	public void setEndTime(Instant endTime) {
		this.endTime = endTime;
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
}
