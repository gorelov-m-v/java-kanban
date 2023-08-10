package model.constant;

public enum Keys {
    TASKS("tasks"),
    SUBTASKS("subtasks"),
    EPICS("epics"),
    HISTORY("history");

    private final String key;

    Keys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}