package model.constant;

public enum TaskStatus {
	NEW("NEW"),
	IN_PROGRESS("IN_PROGRESS"),
	DONE( "DONE");

	private String status;

	TaskStatus(String status) {
		this.status = status;
	}
}
