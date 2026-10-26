
package com.taskmanager.app;

import com.taskmanager.model.Priority;
import com.taskmanager.model.Task;
import com.taskmanager.service.TaskService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class SwingTaskManager extends JFrame {

    private final TaskService service;
    private final DefaultTableModel tableModel;
    private final JTable taskTable;

    private final JTextField searchField = new JTextField(14);

    private final JComboBox<String> priorityFilter =
            new JComboBox<>(new String[]{
                    "ALL", "LOW", "MEDIUM", "HIGH"
            });

    private final JComboBox<String> statusFilter =
            new JComboBox<>(new String[]{
                    "ALL", "PENDING", "COMPLETED"
            });

    private final JTextField dueDateFilter = new JTextField(10);

    public SwingTaskManager() throws IOException {

        service = new TaskService(
                Paths.get("data", "tasks.dat")
        );

        setTitle("Personal Task Manager");
        setSize(1100, 650);
        setMinimumSize(new Dimension(850, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Heading
        JLabel heading = new JLabel("Personal Task Manager");
        heading.setFont(new Font("SansSerif", Font.BOLD, 26));
        heading.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        // Table
        String[] columns = {
                "ID", "Title", "Description",
                "Due Date", "Priority", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        taskTable = new JTable(tableModel);
        taskTable.setRowHeight(28);
        taskTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        taskTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(taskTable);

        // Search and filter controls
        JPanel filterPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 8)
        );

        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);

        filterPanel.add(new JLabel("Priority:"));
        filterPanel.add(priorityFilter);

        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);

        filterPanel.add(new JLabel("Due date:"));
        filterPanel.add(dueDateFilter);

        JButton applyFilterButton = new JButton("Apply Filters");
        JButton clearFilterButton = new JButton("Clear");

        filterPanel.add(applyFilterButton);
        filterPanel.add(clearFilterButton);

        // CRUD buttons
        JButton addButton = new JButton("Add Task");
        JButton updateButton = new JButton("Update Task");
        JButton deleteButton = new JButton("Delete Task");
        JButton refreshButton = new JButton("Refresh");

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 12, 10)
        );

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // Top layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(heading, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        addButton.addActionListener(e -> addTask());
        updateButton.addActionListener(e -> updateTask());
        deleteButton.addActionListener(e -> deleteTask());
        refreshButton.addActionListener(e -> refreshTasks());

        applyFilterButton.addActionListener(e -> refreshTasks());

        clearFilterButton.addActionListener(e -> {
            searchField.setText("");
            priorityFilter.setSelectedItem("ALL");
            statusFilter.setSelectedItem("ALL");
            dueDateFilter.setText("");
            refreshTasks();
        });

        // Press Enter in the search field to search
        searchField.addActionListener(e -> refreshTasks());

        refreshTasks();
    }

    // READ: Display tasks with current filters
    private void refreshTasks() {

        tableModel.setRowCount(0);

        String query = searchField.getText()
                .trim()
                .toLowerCase();

        String selectedPriority =
                (String) priorityFilter.getSelectedItem();

        String selectedStatus =
                (String) statusFilter.getSelectedItem();

        String dateText = dueDateFilter.getText().trim();
        LocalDate filterDate = null;

        if (!dateText.isEmpty()) {
            try {
                filterDate = LocalDate.parse(dateText);
            } catch (DateTimeParseException e) {
                showError(
                        "Invalid due date. Use YYYY-MM-DD, e.g. 2026-12-31."
                );
                return;
            }
        }

        List<Task> tasks = service.getAllTasks();
        int visibleCount = 0;

        for (Task task : tasks) {

            String searchableText =
                    (task.getTitle() + " " + task.getDescription())
                            .toLowerCase();

            if (!searchableText.contains(query)) {
                continue;
            }

            if (!"ALL".equals(selectedPriority)
                    && !task.getPriority().name()
                            .equals(selectedPriority)) {
                continue;
            }

            if ("PENDING".equals(selectedStatus)
                    && task.isCompleted()) {
                continue;
            }

            if ("COMPLETED".equals(selectedStatus)
                    && !task.isCompleted()) {
                continue;
            }

            if (filterDate != null
                    && !task.getDueDate().equals(filterDate)) {
                continue;
            }

            tableModel.addRow(new Object[]{
                    task.getId(),
                    task.getTitle(),
                    task.getDescription(),
                    task.getDueDate(),
                    task.getPriority(),
                    task.isCompleted() ? "Completed" : "Pending"
            });

            visibleCount++;
        }

        setTitle(
                "Personal Task Manager - "
                        + visibleCount + " task(s) shown"
        );
    }

    // CREATE: Add a task
    private void addTask() {

        JTextField titleField = new JTextField(20);
        JTextField descriptionField = new JTextField(20);
        JTextField dueDateField = new JTextField(
                LocalDate.now().toString(), 20
        );

        JComboBox<Priority> priorityBox =
                new JComboBox<>(Priority.values());

        Object[] fields = {
                "Title:", titleField,
                "Description:", descriptionField,
                "Due Date (YYYY-MM-DD):", dueDateField,
                "Priority:", priorityBox
        };

        int result = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Add Task",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String title = titleField.getText().trim();
            String description = descriptionField.getText().trim();

            LocalDate dueDate = LocalDate.parse(
                    dueDateField.getText().trim()
            );

            Priority priority =
                    (Priority) priorityBox.getSelectedItem();

            service.addTask(
                    title, description, dueDate, priority
            );

            refreshTasks();
            showMessage("Task added successfully!");

        } catch (DateTimeParseException e) {
            showError("Invalid date. Use YYYY-MM-DD.");
        } catch (IllegalArgumentException e) {
            showError("Invalid input: " + e.getMessage());
        } catch (IOException e) {
            showError("Unable to save task: " + e.getMessage());
        }
    }

    // UPDATE: Modify the selected task
    private void updateTask() {

        int row = taskTable.getSelectedRow();

        if (row == -1) {
            showError("Please select a task to update.");
            return;
        }

        // Convert sorted table row to its actual model row
        int modelRow = taskTable.convertRowIndexToModel(row);
        int id = (Integer) tableModel.getValueAt(modelRow, 0);

        Task task = service.findTask(id).orElse(null);

        if (task == null) {
            showError("Task not found.");
            refreshTasks();
            return;
        }

        JTextField titleField = new JTextField(task.getTitle(), 20);
        JTextField descriptionField =
                new JTextField(task.getDescription(), 20);
        JTextField dueDateField =
                new JTextField(task.getDueDate().toString(), 20);

        JComboBox<Priority> priorityBox =
                new JComboBox<>(Priority.values());
        priorityBox.setSelectedItem(task.getPriority());

        JCheckBox completedBox =
                new JCheckBox("Completed", task.isCompleted());

        Object[] fields = {
                "Title:", titleField,
                "Description:", descriptionField,
                "Due Date (YYYY-MM-DD):", dueDateField,
                "Priority:", priorityBox,
                completedBox
        };

        int result = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Update Task",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            boolean updated = service.updateTask(
                    id,
                    titleField.getText().trim(),
                    descriptionField.getText().trim(),
                    LocalDate.parse(dueDateField.getText().trim()),
                    (Priority) priorityBox.getSelectedItem(),
                    completedBox.isSelected()
            );

            if (updated) {
                refreshTasks();
                showMessage("Task updated successfully!");
            } else {
                showError("Task not found.");
            }

        } catch (DateTimeParseException e) {
            showError("Invalid date. Use YYYY-MM-DD.");
        } catch (IllegalArgumentException e) {
            showError("Invalid input: " + e.getMessage());
        } catch (IOException e) {
            showError("Unable to save update: " + e.getMessage());
        }
    }

    // DELETE: Remove the selected task
    private void deleteTask() {

        int row = taskTable.getSelectedRow();

        if (row == -1) {
            showError("Please select a task to delete.");
            return;
        }

        int modelRow = taskTable.convertRowIndexToModel(row);
        int id = (Integer) tableModel.getValueAt(modelRow, 0);

        String title = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete task \"" + title + "\"?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (service.deleteTask(id)) {
                refreshTasks();
                showMessage("Task deleted successfully!");
            } else {
                showError("Task not found.");
            }
        } catch (IOException e) {
            showError("Unable to delete task: " + e.getMessage());
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Personal Task Manager",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );

                SwingTaskManager app = new SwingTaskManager();
                app.setVisible(true);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Unable to initialize task storage:\n"
                                + e.getMessage(),
                        "Startup Error",
                        JOptionPane.ERROR_MESSAGE
                );
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Unable to start application:\n"
                                + e.getMessage(),
                        "Startup Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
