package model.constant;

public enum TaskType {
    TASK("TASK"),
    EPIC("EPIC"),
    SUBTASK("SUBTASK");

    private String name;

    TaskType(String name) {
        this.name = name;
    }
}
