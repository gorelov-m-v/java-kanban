package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
	private List<Subtask> subtasks = new ArrayList<>();

	public Epic(String title, String description, String status) {
		super(title, description, status);
	}

	public List<Subtask> getSubtasks() {
		return subtasks;
	}

	public void addSubtask(Subtask subtask) {
		subtasks.add(subtask);
	}

	public void removeSubtask(Subtask subtask) {
		subtasks.remove(subtask);
	}
}
