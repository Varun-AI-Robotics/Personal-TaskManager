
package com.taskmanager.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Priority priority;
    private boolean completed;

    public Task(int id, String title, String description,
                LocalDate dueDate, Priority priority,
                boolean completed) {

        this.id = id;
        setTitle(title);
        setDescription(description);
        setDueDate(dueDate);
        setPriority(priority);
        this.completed = completed;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Title cannot be empty."
            );
        }
        this.title = title.trim();
    }

    public void setDescription(String description) {
        if (description == null) {
            throw new IllegalArgumentException(
                "Description cannot be null."
            );
        }
        this.description = description.trim();
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = Objects.requireNonNull(
            dueDate, "Due date is required."
        );
    }

    public void setPriority(Priority priority) {
        this.priority = Objects.requireNonNull(
            priority, "Priority is required."
        );
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "ID: " + id
                + "\nTitle: " + title
                + "\nDescription: " + description
                + "\nDue Date: " + dueDate
                + "\nPriority: " + priority
                + "\nStatus: "
                + (completed ? "Completed" : "Pending");
    }
}
