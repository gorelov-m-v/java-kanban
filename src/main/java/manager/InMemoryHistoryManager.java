package manager;

import model.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> customLinkedList = new HashMap<>();
    private Node head;
    private Node tail;

    private Node linkLast(Task task) {
        final Node newTail = tail;
        final Node newNode = new Node(newTail, task, null);
        tail = newNode;
        if (newTail != null) {
            newTail.next = newNode;
        }
        else {
            head = newNode;
        }
        return newNode;
    }

    private void removeNode(Node node) {
        final Node next = node.next;
        final Node prev = node.prev;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }
    }

    public List<Task> getTasks() {
        List<Task> allListHistory = new ArrayList<>();
        Node node = head;
        while (node != null) {
            allListHistory.add(node.task);
            node = node.next;
        }
        return allListHistory;
    }

    @Override
    public void removeById(int id) {
        if (customLinkedList.containsKey(id)) {
            removeNode(customLinkedList.get(id));
            customLinkedList.remove((id));
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        if(customLinkedList.containsKey(task.getId())) {
            removeNode(customLinkedList.get(task.getId()));
            customLinkedList.remove(task.getId());
        }
            Node newNode = linkLast(task);
            customLinkedList.put(task.getId(), newNode);
    }
}
