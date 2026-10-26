
package com.taskmanager.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void validTaskStoresItsFields() {
        Task task = new Task(
                1,
                "Study Java",
                "Practice OOP",
                LocalDate.of(2026, 12, 31),
                Priority.HIGH,
                false
        );

        assertEquals("Study Java", task.getTitle());
        assertEquals("Practice OOP", task.getDescription());
        assertEquals(Priority.HIGH, task.getPriority());
        assertFalse(task.isCompleted());
    }

    @Test
    void emptyTitleIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new Task(
                        1, " ", "Description",
                        LocalDate.now(), Priority.LOW, false
                )
        );
    }

    @Test
    void nullDueDateIsRejected() {
        assertThrows(NullPointerException.class, () ->
                new Task(
                        1, "Study", "Description",
                        null, Priority.LOW, false
                )
        );
    }

    @Test
    void nullPriorityIsRejected() {
        assertThrows(NullPointerException.class, () ->
                new Task(
                        1, "Study", "Description",
                        LocalDate.now(), null, false
                )
        );
    }
}
