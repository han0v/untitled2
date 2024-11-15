package com.faiz.tasktreker.service;

import com.faiz.tasktreker.exceptions.ValidationException;
import com.faiz.tasktreker.model.Epic;
import com.faiz.tasktreker.model.SubTask;
import com.faiz.tasktreker.model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int uniqId = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager;

    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public InMemoryTaskManager() {
        this(Managers.getDefaultHistory());
    }


    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            historyManager.add(subTask);
        }
        return subTask;

    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }


    @Override
    public void delAll() {
        if (tasks.isEmpty() && epics.isEmpty() && subTasks.isEmpty()) {
            System.out.println("Нет задач для удаления! Трекер задач пуст!");
            return;
        }

        if (!tasks.isEmpty()) {
            tasks.keySet().forEach(taskId -> historyManager.remove(taskId));
            tasks.clear();
            System.out.println("Список задач очищен!");
        }

        if (!epics.isEmpty()) {
            epics.values().forEach(epic -> {
                epic.getSubTaskList().values().forEach(subTask -> historyManager.remove(subTask.getId()));
                historyManager.remove(epic.getId());
            });
            epics.clear();
            System.out.println("Список эпиков очищен!");
        }

        if (!subTasks.isEmpty()) {
            subTasks.keySet().forEach(subTaskId -> historyManager.remove(subTaskId));
            subTasks.clear();
            System.out.println("Список подзадач очищен!");
        }

        prioritizedTasks.clear();
    }

    @Override
    public void delAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст!");
        } else {
            tasks.keySet().forEach(taskId -> {
                historyManager.remove(taskId);
                prioritizedTasks.remove(tasks.get(taskId));
            });
            tasks.clear();
            System.out.println("Список задач очищен!");
        }
    }

    @Override
    public void delAllEpics() {
        if (epics.isEmpty()) {
            System.out.println("Список эпиков пуст!");
        } else {
            epics.values().forEach(epic -> {
                epic.getSubTaskList().values().forEach(subTask -> {
                    historyManager.remove(subTask.getId());
                    prioritizedTasks.remove(subTask);
                });
                historyManager.remove(epic.getId());
                prioritizedTasks.remove(epic);
            });
            epics.clear();
            subTasks.clear();
            System.out.println("Список эпиков очищен!");
        }
    }


    @Override
    public void delAllSubTasks() {
        if (subTasks.isEmpty()) {
            System.out.println("Список подзадач пуст!");
        } else {
            subTasks.values().forEach(subTask -> {
                historyManager.remove(subTask.getId());
                prioritizedTasks.remove(subTask);
            });
            epics.values().forEach(epic -> {
                epic.getSubTaskList().clear();
                epic.updateStatus();
            });
            subTasks.clear();
            System.out.println("Список подзадач очищен!");
        }
    }


    @Override
    public Task getById(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            historyManager.add(task);
            return task;
        }
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            historyManager.add(epic);
            return epic;
        }
        if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            historyManager.add(subTask);
            return subTask;
        }
        return null;
    }


    @Override
    public Task createTask(Task task) {
        task.setId(getNextUniqId());
        validation(task);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        System.out.println("Задача с ID " + task.getId() + " добавлена.");
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(getNextUniqId());
        epics.put(epic.getId(), epic);
        System.out.println("Эпик с ID " + epic.getId() + " добавлен.");
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        if (epics.containsKey(subTask.getEpicId())) {
            validation(subTask);
            subTask.setId(getNextUniqId());
            subTasks.put(subTask.getId(), subTask);
            Epic epic = epics.get(subTask.getEpicId());
            epic.createSubTask(subTask);
            epic.updateStatus();
            prioritizedTasks.add(subTask);
            System.out.println("Подзадача с ID " + subTask.getId() + " добавлена к эпику с ID " + epic.getId());
        } else {
            System.out.println("Ошибка: Эпик с ID " + subTask.getEpicId() + " не найден.");
        }
        return subTask;
    }


    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            validation(task);
            prioritizedTasks.remove(tasks.get(task.getId()));
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        } else {
            System.out.println("Задача не найдена!");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic oldEpic = epics.get(epic.getId());
            oldEpic.setDescription(epic.getDescription());
            oldEpic.setName(epic.getName());
        } else {
            System.out.println("Эпик не найден!");
        }
    }

    @Override
    public void updateSubTask(SubTask subtask) {
        if (subTasks.containsKey(subtask.getId())) {
            validation(subtask);
            prioritizedTasks.remove(subTasks.get(subtask.getId()));
            subTasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            epic.updateEpicData();
            epic.updateStatus();
            prioritizedTasks.add(subtask);
        } else {
            System.out.println("Подзадача не найдена!");
        }
    }


    @Override
    public void delById(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.remove(id);
            historyManager.remove(id);
            prioritizedTasks.remove(task);
            System.out.println("Задача с ID " + id + " удалена.");
            return;
        }

        if (epics.containsKey(id)) {
            Epic epic = epics.remove(id);
            epic.getSubTaskList().values().forEach(subTask -> {
                subTasks.remove(subTask.getId());
                historyManager.remove(subTask.getId());
                prioritizedTasks.remove(subTask);
            });
            historyManager.remove(id);
            prioritizedTasks.remove(epic);
            System.out.println("Эпик с ID " + id + " и его подзадачи удалены.");
            return;
        }

        SubTask subTask = subTasks.remove(id);
        if (subTask != null) {
            Epic parentEpic = epics.get(subTask.getEpicId());
            if (parentEpic != null) {
                parentEpic.getSubTaskList().remove(subTask.getId());
                parentEpic.updateStatus();
                historyManager.remove(id);
                prioritizedTasks.remove(subTask);
                System.out.println("Подзадача с ID " + id + " удалена из эпика.");
            } else {
                System.out.println("Эпик для подзадачи с ID " + id + " не найден.");
            }
            return;
        }

        System.out.println("Задачи с ID " + id + " не существует.");
    }


    @Override
    public List<SubTask> getSubTaskList(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            return new ArrayList<>(epic.getSubTaskList().values());
        }
        return List.of();
    }


    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public Set<Task> getPrioritizedTasks() {
        return new TreeSet<>(prioritizedTasks);
    }

    protected void addToPrioritizedTasks(Task task) {
        prioritizedTasks.add(task);
    }

    private void validation(Task task) {
        if (task.getStartTime() == null || task.getEndTime() == null) {
            return;
        }

        for (Task existTask : prioritizedTasks) {
            if (Objects.equals(task.getId(), existTask.getId())) {
                continue;
            }

            if (existTask.getStartTime() == null) {
                return;
            }

            boolean doesNotOverlap =
                    task.getEndTime().isBefore(existTask.getStartTime()) ||
                            task.getStartTime().isAfter(existTask.getEndTime()) ||
                            task.getEndTime().isEqual(existTask.getStartTime()) ||
                            task.getStartTime().isEqual(existTask.getEndTime());

            if (!doesNotOverlap) {
                throw new ValidationException("Задача " + task + " пересекается с задачей " + existTask);
            }
        }
    }



    protected int getNextUniqId() {
        return uniqId++;
    }
}
