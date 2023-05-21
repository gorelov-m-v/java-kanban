package model;

public class Subtask extends Task {
//	private Epic epic;

	public Subtask(String title, String description, Epic epic) {
		super(title, description);
	}

	public Subtask(String title, String description, Epic epic, String status) {
		super(title, description, status);
	}

	public Subtask(int id, String title, String description, String status) {
		super(id, title, description, status);
	}

}
