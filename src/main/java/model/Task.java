package model;

import lombok.Getter;
import lombok.Setter;
import model.constants.TaskStatuses;

@Getter @Setter
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
