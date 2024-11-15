package com.faiz.tasktreker.model;

import com.faiz.tasktreker.service.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EpicTest {
    InMemoryTaskManager taskManagerOne = new InMemoryTaskManager();
    Epic epic1;
    SubTask subTask1;
    SubTask subTask2;

    @BeforeEach
    void beforeEach() {
        epic1 = new Epic("Test description", "TestEpic");
        taskManagerOne.createEpic(epic1);
        subTask1 = new SubTask("Test description", "TestSubTask", epic1.getId(),
                LocalDateTime.of(2022, 8, 9, 12, 0), Duration.ofMinutes(30));
        taskManagerOne.createSubTask(subTask1);
        subTask2 = new SubTask("Test description", "TestSubTask", epic1.getId(),
                LocalDateTime.of(2022, 8, 10, 12, 0), Duration.ofMinutes(30));
        taskManagerOne.createSubTask(subTask2);
    }


    @Test
    void addNewEpic() {
        Epic epic2 = new Epic("Test description", "TestEpic");
        taskManagerOne.createEpic(epic2);
        assertEquals(epic2.getStatus(),Status.NEW);
    }

    @Test
    void addEpicWithSubTasksNew() {
        assertNotNull(taskManagerOne.getSubTaskById(subTask1.getId()), "Подзадача 1 не найдена.");
        assertNotNull(taskManagerOne.getSubTaskById(subTask2.getId()), "Подзадача 2 не найдена.");
        assertEquals(taskManagerOne.getSubTasks().size(), 2, "Ожидаемое количество подзадач не соответствует фактическому");
        assertEquals(subTask1.getEpicId(), epic1.getId(), "ID подзадачи должен совпадать с ID соответствующего эпика");
        assertEquals(epic1.getStatus(), Status.NEW, "Статус эпика должен быть NEW, так как все подзадачи новые");
    }

    @Test
    void addEpicWithSubTasksDone() {
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        epic1.updateStatus();


        assertNotNull(taskManagerOne.getSubTaskById(subTask1.getId()), "Подзадача 1 не найдена.");
        assertNotNull(taskManagerOne.getSubTaskById(subTask2.getId()), "Подзадача 2 не найдена.");
        assertEquals(taskManagerOne.getSubTasks().size(), 2, "Ожидаемое количество подзадач не соответствует фактическому");
        assertEquals(subTask1.getEpicId(), epic1.getId(), "ID подзадачи должен совпадать с ID соответствующего эпика");
        assertEquals(epic1.getStatus(), Status.DONE, "Статус эпика должен быть DONE, так как все подзадачи завершены");
    }

    @Test
    void addEpicWithSubTasksNewAndDone() {
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.NEW);
        epic1.updateStatus();

        assertNotNull(taskManagerOne.getSubTaskById(subTask1.getId()), "Подзадача 1 не найдена.");
        assertNotNull(taskManagerOne.getSubTaskById(subTask2.getId()), "Подзадача 2 не найдена.");
        assertEquals(taskManagerOne.getSubTasks().size(), 2, "Ожидаемое количество подзадач не соответствует фактическому");
        assertEquals(subTask1.getEpicId(), epic1.getId(), "ID подзадачи должен совпадать с ID соответствующего эпика");
        assertEquals(epic1.getStatus(), Status.IN_PROGRESS, "Статус эпика должен быть IN_PROGRESS, так как одна подзадача завершена, а другая новая");
    }

    @Test
    void addEpicWithSubTasksInProgress() {
        subTask1.setStatus(Status.IN_PROGRESS);
        subTask2.setStatus(Status.IN_PROGRESS);
        epic1.updateStatus();

        assertNotNull(taskManagerOne.getSubTaskById(subTask1.getId()), "Подзадача 1 не найдена.");
        assertNotNull(taskManagerOne.getSubTaskById(subTask2.getId()), "Подзадача 2 не найдена.");
        assertEquals(taskManagerOne.getSubTasks().size(), 2, "Ожидаемое количество подзадач не соответствует фактическому");
        assertEquals(subTask1.getEpicId(), epic1.getId(), "ID подзадачи должен совпадать с ID соответствующего эпика.");
        assertEquals(epic1.getStatus(), Status.IN_PROGRESS, "Статус эпика должен быть IN_PROGRESS, так как обе подзадачи в процессе выполнения.");
    }

}