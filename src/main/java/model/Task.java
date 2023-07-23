package model;

import model.constant.TaskStatus;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Task {
	static AtomicInteger nextId = new AtomicInteger();
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Task task = (Task) o;

		if (id != task.id) return false;
		if (duration != task.duration) return false;
		if (!Objects.equals(title, task.title)) return false;
		if (!Objects.equals(description, task.description)) return false;
		if (status != task.status) return false;
		return Objects.equals(startTime, task.startTime);
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + (title != null ? title.hashCode() : 0);
		result = 31 * result + (description != null ? description.hashCode() : 0);
		result = 31 * result + (status != null ? status.hashCode() : 0);
		result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
		result = 31 * result + (int) (duration ^ (duration >>> 32));
		return result;
	}
}
