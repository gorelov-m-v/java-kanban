package model.constants;

public enum TaskStatus {
	NEW(1, "NEW"),
	IN_PROGRESS(2, "IN_PROGRESS"),
	DONE(3, "DONE");

	private int statusId;
	private String status;

	TaskStatus(int statusId, String status) {
		this.statusId = statusId;
		this.status = status;
	}
}
