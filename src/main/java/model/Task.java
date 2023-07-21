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

	public Task(String title, String description) {
		this.title = title;
		this.description = description;
		this.status = TaskStatus.NEW;
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
				", status='" + status + '\'' +
				'}';
	}
}
