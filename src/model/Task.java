package model;

import model.constants.TaskStatuses;

public class Task {
	private int id;
	private String title;
	private String description;
	private TaskStatuses status = TaskStatuses.NEW;

	public Task(String title, String description) {
		this.title = title;
		this.description = description;
	}

	public Task(String title, String description, TaskStatuses status) {
		this.title = title;
		this.description = description;
		this.status = status;
	}

	public Task(int id, String title, String description, TaskStatuses status) {
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

	public TaskStatuses getStatus() {
		return status;
	}

	public void setStatus(TaskStatuses status) {
		this.status = status;
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
