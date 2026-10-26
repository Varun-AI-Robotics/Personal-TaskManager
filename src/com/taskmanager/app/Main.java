
package com.taskmanager.app;

import com.taskmanager.model.Priority;
import com.taskmanager.model.Task;
import com.taskmanager.service.TaskService;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static TaskService service;

    public static void main(String[] args) {

        try {
            service = new TaskService(
                    Paths.get("data", "tasks.dat")
            );

            System.out.println("==============================");
            System.out.println("   PERSONAL TASK MANAGER");
            System.out.println("==============================");

            boolean running = true;

            while (running) {
                System.out.println("\n1. Add Task");
                System.out.println("2. View All Tasks");
                System.out.println("3. Update Task");
                System.out.println("4. Delete Task");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");

                String choice = sc.nextLine().trim();

                try {
                    switch (choice) {
                        case "1":
                            addTask();
                            break;
                        case "2":
                            viewTasks();
                            break;
                        case "3":
                            updateTask();
                            break;
                        case "4":
                            deleteTask();
                            break;
                        case "5":
                            running = false;
                            System.out.println("Thank you for using Task Manager!");
                            break;
                        default:
                            System.out.println(
                                    "Invalid choice! Enter 1 to 5."
                            );
                    }
                } catch (IOException e) {
                    System.out.println(
                            "Storage error: " + e.getMessage()
                    );
                } catch (IllegalArgumentException e) {
                    System.out.println(
                            "Invalid input: " + e.getMessage()
                    );
                }
            }

        } catch (IOException e) {
            System.out.println(
                    "Unable to initialize task storage: "
                            + e.getMessage()
            );
        } finally {
            sc.close();
        }
    }

    // CREATE
    private static void addTask() throws IOException {

        System.out.println("\n--- ADD TASK ---");

        String title = readNonEmpty("Enter title: ");

        System.out.print("Enter description: ");
        String description = sc.nextLine().trim();

        LocalDate dueDate = readDate();
        Priority priority = readPriority();

        Task task = service.addTask(
                title, description, dueDate, priority
        );

        System.out.println("\nTask added successfully!");
        System.out.println(task);
    }

    // READ
    private static void viewTasks() {

        System.out.println("\n--- ALL TASKS ---");

        List<Task> tasks = service.getAllTasks();

        if (tasks.isEmpty()) {
            System.out.println("No tasks available.");
            return;
        }

        for (Task task : tasks) {
            System.out.println("------------------------------");
            System.out.println(task);
        }

        System.out.println("------------------------------");
        System.out.println("Total tasks: " + tasks.size());
    }

    // UPDATE
    private static void updateTask() throws IOException {

        System.out.println("\n--- UPDATE TASK ---");

        int id = readPositiveInt("Enter task ID: ");

        Task existing = service.findTask(id).orElse(null);

        if (existing == null) {
            System.out.println("Task ID not found!");
            return;
        }

        System.out.println("Current task:");
        System.out.println(existing);

        System.out.println("\nEnter updated details:");

        String title = readNonEmpty("New title: ");

        System.out.print("New description: ");
        String description = sc.nextLine().trim();

        LocalDate dueDate = readDate();
        Priority priority = readPriority();

        boolean completed = readCompletion();

        boolean updated = service.updateTask(
                id, title, description,
                dueDate, priority, completed
        );

        if (updated) {
            System.out.println("Task updated successfully!");
        } else {
            System.out.println("Task ID not found!");
        }
    }

    // DELETE
    private static void deleteTask() throws IOException {

        System.out.println("\n--- DELETE TASK ---");

        int id = readPositiveInt("Enter task ID: ");

        Task existing = service.findTask(id).orElse(null);

        if (existing == null) {
            System.out.println("Task ID not found!");
            return;
        }

        System.out.println(existing);
        System.out.print("Confirm deletion? (yes/no): ");

        String confirmation = sc.nextLine().trim();

        if (confirmation.equalsIgnoreCase("yes")) {
            if (service.deleteTask(id)) {
                System.out.println("Task deleted successfully!");
            } else {
                System.out.println("Task could not be deleted.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    // INPUT VALIDATION
    private static String readNonEmpty(String message) {

        while (true) {
            System.out.print(message);
            String value = sc.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("This field cannot be empty.");
        }
    }

    private static int readPositiveInt(String message) {

        while (true) {
            System.out.print(message);

            try {
                int value = Integer.parseInt(
                        sc.nextLine().trim()
                );

                if (value > 0) {
                    return value;
                }

                System.out.println("ID must be a positive number.");

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private static LocalDate readDate() {

        while (true) {
            System.out.print("Due date (YYYY-MM-DD): ");

            try {
                return LocalDate.parse(
                        sc.nextLine().trim()
                );

            } catch (DateTimeParseException e) {
                System.out.println(
                        "Invalid date! Example: 2026-12-31"
                );
            }
        }
    }

    private static Priority readPriority() {

        while (true) {
            System.out.print("Priority (LOW/MEDIUM/HIGH): ");

            try {
                return Priority.valueOf(
                        sc.nextLine().trim().toUpperCase()
                );

            } catch (IllegalArgumentException e) {
                System.out.println(
                        "Choose LOW, MEDIUM, or HIGH."
                );
            }
        }
    }

    private static boolean readCompletion() {

        while (true) {
            System.out.print("Completed? (yes/no): ");

            String value = sc.nextLine().trim();

            if (value.equalsIgnoreCase("yes")) {
                return true;
            }

            if (value.equalsIgnoreCase("no")) {
                return false;
            }

            System.out.println("Please enter yes or no.");
        }
    }
}
