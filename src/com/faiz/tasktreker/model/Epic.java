package com.faiz.tasktreker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Epic extends Task {
    private final Map<Integer, SubTask> subTaskList;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        subTaskList = new HashMap<>();
    }

    public Epic(String name, String description, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        subTaskList = new HashMap<>();
    }

    public Map<Integer, SubTask> getSubTaskList() {
        return subTaskList;
    }

    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void createSubTask(SubTask sub) {
        subTaskList.put(sub.getId(), sub);
        sub.setEpicId(this.getId());
    }

    public void updateStatus() {
        if (this.subTaskList.isEmpty()) {
            this.setStatus(Status.NEW);
        } else {
            boolean allDone = true;
            boolean allNew = true;

            for (SubTask sub : subTaskList.values()) {
                if (!sub.getStatus().equals(Status.DONE)) {
                    allDone = false;
                }
                if (!sub.getStatus().equals(Status.NEW)) {
                    allNew = false;
                }
            }

            if (allNew) {
                this.setStatus(Status.NEW);
            } else if (allDone) {
                this.setStatus(Status.DONE);
            } else {
                this.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    public void updateEpicData() {
        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;
        Duration totalDuration = null;  // Сначала totalDuration равен null

        for (SubTask sub : subTaskList.values()) {
            LocalDateTime subStart = sub.getStartTime();
            Duration subDuration = sub.getDuration();

            if (earliestStart == null || (subStart != null && subStart.isBefore(earliestStart))) {
                earliestStart = subStart;
            }

            LocalDateTime subEnd = sub.getEndTime();
            if (latestEnd == null || (subEnd != null && subEnd.isAfter(latestEnd))) {
                latestEnd = subEnd;
            }

            // Устанавливаем totalDuration с первого значения подзадачи
            if (subDuration != null) {
                if (totalDuration == null) {
                    totalDuration = subDuration;  //значение на первое время
                } else {
                    totalDuration = totalDuration.plus(subDuration);  // Добавляем если уже инициализировано
                }
            }
        }

        // Устанавливаем начальное время, конечное время и общую продолжительность
        setStartTime(earliestStart);
        this.endTime = latestEnd;
        setDuration(totalDuration);  // totalDuration может быть null, если не было ни одной подзадачи с продолжительностью
    }


    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;

        Epic epic = (Epic) o;
        return Objects.equals(getId(), epic.getId()) &&
                Objects.equals(getName(), epic.getName()) &&
                Objects.equals(getDescription(), epic.getDescription()) &&
                getStatus() == epic.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getStatus());
    }

    @Override
    public String toString() {
        return getId() + "," + TaskType.EPIC + "," + getName() + "," + getStatus() + "," + getEpicId() + ","
                + getDescription() + "," + getStartTime() + "," + getDuration().toMinutes() + ",";
    }
}


