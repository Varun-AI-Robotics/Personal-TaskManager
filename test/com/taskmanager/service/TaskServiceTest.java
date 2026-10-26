
package com.taskmanager.service;

import com.taskmanager.model.Priority;
import com.taskmanager.model.Task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void taskPersistsAfterServiceRestart() throws IOException {
        Path file = tempDir.resolve("tasks.dat");

        TaskService firstService = new TaskService(file);

        firstService.addTask(
                "Complete project",
                "Write tests",
                LocalDate.of(2026, 12, 31),
                Priority.HIGH
        );

        TaskService restartedService = new TaskService(file);

        assertEquals(1, restartedService.getAllTasks().size());

        Task saved = restartedService.getAllTasks().get(0);

        assertEquals("Complete project", saved.getTitle());
        assertEquals(Priority.HIGH, saved.getPriority());
    }

    @Test
    void deletedTaskStaysDeletedAfterRestart() throws IOException {
        Path file = tempDir.resolve("tasks.dat");
        TaskService service = new TaskService(file);

        Task task = service.addTask(
                "Temporary task", "",
                LocalDate.now(), Priority.LOW
        );

        assertTrue(service.deleteTask(task.getId()));

        TaskService restartedService = new TaskService(file);

        assertTrue(restartedService.getAllTasks().isEmpty());
    }

    @Test
    void missingFileStartsWithEmptyTaskList() throws IOException {
        Path file = tempDir.resolve("new-folder/tasks.dat");

        TaskService service = new TaskService(file);

        assertTrue(service.getAllTasks().isEmpty());
    }
}
