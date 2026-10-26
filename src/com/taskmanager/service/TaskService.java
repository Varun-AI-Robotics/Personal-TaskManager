
package com.taskmanager.service;

import com.taskmanager.model.Priority;
import com.taskmanager.model.Task;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TaskService {

    private final Path storageFile;
    private final List<Task> tasks = new ArrayList<>();
    private int nextId = 1;

    public TaskService(Path storageFile) throws IOException {
        this.storageFile = storageFile;
        loadTasks();
    }

    // READ: View all tasks
    public List<Task> getAllTasks() {
        List<Task> copy = new ArrayList<>(tasks);
        copy.sort(Comparator.comparingInt(Task::getId));
        return copy;
    }

    // CREATE: Add a new task
    public Task addTask(String title, String description,
                        LocalDate dueDate, Priority priority)
            throws IOException {

        Task task = new Task(
                nextId, title, description, dueDate, priority, false
        );

        tasks.add(task);
        nextId++;

        try {
            saveTasks();
        } catch (IOException e) {
            tasks.remove(task);
            nextId--;
            throw e;
        }

        return task;
    }

    // Find task by ID
    public Optional<Task> findTask(int id) {
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst();
    }

    // UPDATE: Modify an existing task
    public boolean updateTask(int id, String title,
                              String description, LocalDate dueDate,
                              Priority priority, boolean completed)
            throws IOException {

        Optional<Task> result = findTask(id);

        if (!result.isPresent()) {
            return false;
        }

        Task task = result.get();

        // Validate the new values before modifying the task.
        Task validated = new Task(
                id, title, description, dueDate, priority, completed
        );

        String oldTitle = task.getTitle();
        String oldDescription = task.getDescription();
        LocalDate oldDueDate = task.getDueDate();
        Priority oldPriority = task.getPriority();
        boolean oldCompleted = task.isCompleted();

        task.setTitle(validated.getTitle());
        task.setDescription(validated.getDescription());
        task.setDueDate(validated.getDueDate());
        task.setPriority(validated.getPriority());
        task.setCompleted(validated.isCompleted());

        try {
            saveTasks();
        } catch (IOException e) {
            task.setTitle(oldTitle);
            task.setDescription(oldDescription);
            task.setDueDate(oldDueDate);
            task.setPriority(oldPriority);
            task.setCompleted(oldCompleted);
            throw e;
        }

        return true;
    }

    // DELETE: Remove a task
    public boolean deleteTask(int id) throws IOException {
        Optional<Task> result = findTask(id);

        if (!result.isPresent()) {
            return false;
        }

        Task task = result.get();
        tasks.remove(task);

        try {
            saveTasks();
        } catch (IOException e) {
            tasks.add(task);
            throw e;
        }

        return true;
    }

    // Load tasks from the local file
    private void loadTasks() throws IOException {

        if (!Files.exists(storageFile)) {
            Path parent = storageFile.toAbsolutePath().getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            return; // First run: no saved tasks yet.
        }

        if (Files.size(storageFile) == 0) {
            return;
        }

        try (ObjectInputStream input = new ObjectInputStream(
                new BufferedInputStream(
                        Files.newInputStream(storageFile)))) {

            Object data = input.readObject();

            if (!(data instanceof List<?>)) {
                throw new IOException(
                        "Invalid task data format."
                );
            }

            for (Object item : (List<?>) data) {
                if (!(item instanceof Task)) {
                    throw new IOException(
                            "Invalid task record found in file."
                    );
                }

                Task task = (Task) item;
                tasks.add(task);
                nextId = Math.max(nextId, task.getId() + 1);
            }

        } catch (ClassNotFoundException | ClassCastException e) {
            throw new IOException(
                    "Unable to read saved tasks: " + e.getMessage(), e
            );
        } catch (EOFException e) {
            throw new IOException(
                    "Task file is incomplete or corrupted.", e
            );
        }
    }

    // Save tasks to the local file
    private void saveTasks() throws IOException {

        Path parent = storageFile.toAbsolutePath().getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        Path tempFile = storageFile.resolveSibling(
                storageFile.getFileName() + ".tmp"
        );

        try (ObjectOutputStream output = new ObjectOutputStream(
                new BufferedOutputStream(
                        Files.newOutputStream(tempFile)))) {

            output.writeObject(new ArrayList<>(tasks));
        }

        try {
            Files.move(
                    tempFile,
                    storageFile,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            );
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(
                    tempFile,
                    storageFile,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }
}
