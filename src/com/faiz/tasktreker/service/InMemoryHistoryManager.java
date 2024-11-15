package com.faiz.tasktreker.service;

import com.faiz.tasktreker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node<T> {
        public T elem;
        public Node<T> next;
        public Node<T> prev;

        Node(Node<T> prev, T elem, Node<T> next) {
            this.elem = elem;
            this.next = next;
            this.prev = prev;
        }
    }

    private Map<Integer, Node<Task>> history = new HashMap<>();
    private Node<Task> first;
    private Node<Task> last;

    @Override
    public void add(Task task) {
        Node<Task> node = history.get(task.getId());
        removeNode(node);
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        removeNode(history.remove(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    void linkLast(Task task) {
        final Node<Task> latest = last;
        final Node<Task> newNode = new Node<>(latest, task, null);
        last = newNode;
        if (latest == null) {
            first = newNode;
        } else {
            latest.next = newNode;
        }
        history.put(task.getId(), newNode);
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> returnList = new ArrayList<>(history.size());
        Node<Task> current = first;
        while (current != null) {
            returnList.add(current.elem);
            current = current.next;
        }
        return returnList;
    }

    public void removeNode(Node<Task> node) {
        if (node == null) {
            return;
        }

        Node<Task> prevNode = node.prev;
        Node<Task> nextNode = node.next;

        if (prevNode != null) {
            prevNode.next = nextNode;
        } else {
            first = nextNode; // Если это первый элемент
        }

        if (nextNode != null) {
            nextNode.prev = prevNode;
        } else {
            last = prevNode; // Если это последний элемент
        }
    }
}
