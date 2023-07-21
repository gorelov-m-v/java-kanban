package model;

import model.constant.TaskStatus;
import java.time.Instant;

public class Task {

	private int id;
	private String title;
	private String description;
	private TaskStatus status;
	private Instant startTime;
	private long duration;

	public Task(String title, String description, Instant startTime, long duration) {
		this.title = title;
		this.description = description;
		this.status = TaskStatus.NEW;
		this.startTime = startTime;
		this.duration = duration;
	}


	public Task(String title, String description, TaskStatus status) {
		this.title = title;
		this.description = description;
		this.status = status;
	}

	public Task(int id, String title, String description, TaskStatus status) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.status = status;
	}

	public Task(String title, String description) {
		this.title = title;
		this.description = description;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public TaskStatus getStatus() {
		return status;
	}
	public void setStatus(TaskStatus status) {
		this.status = status;
	}
	public Instant getStartTime() {
		return startTime;
	}
	public void setStartTime(Instant startTime) {
		this.startTime = startTime;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}

	public Instant getEndTime() {
		return startTime.plusSeconds(duration * 60);
	}

	public String getEpicId(Task task) {
		if (task.getClass() == Subtask.class) {
			return Integer.toString(((Subtask) task).getEpicId());
		}
		return "";
	}

	@Override
	public String toString() {
		return "Task{" +
				"id=" + id +
				", title='" + title + '\'' +
				", description='" + description + '\'' +
				", status=" + status +
				", startTime=" + startTime +
				", duration=" + duration +
				'}';
	}
}
