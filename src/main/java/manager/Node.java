package manager;

import model.Task;

public class Node {
    public Node prev;
    public Task task;
    public Node next;

    public Node(Node prev, Task value, Node next) {
        this.prev = prev;
        this.task = value;
        this.next = next;
    }
}
