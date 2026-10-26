# Personal Task Manager

A desktop-based task management application developed in **Java** using **Java Swing**. It helps users create, view, update, delete, search, and filter personal tasks. Tasks are saved locally to a file so that data remains available after the application is closed.

## Features

- **Add Tasks:** Create tasks with a title, description, due date, and priority.
- **View Tasks:** Display tasks in a structured table.
- **Update Tasks:** Edit task details and completion status.
- **Delete Tasks:** Remove tasks with confirmation.
- **Local Data Persistence:** Save and load tasks using Java file I/O and serialization.
- **Search Tasks:** Search by task title or description.
- **Filter Tasks:** Filter by priority and completion status.
- **Due-Date Filtering:** Find tasks by a specific due date.
- **Input Validation:** Validate required fields and date formats.
- **Object-Oriented Design:** Organize code into model, service, and application packages.

## Technologies Used

- **Language:** Java
- **GUI:** Java Swing
- **Data Storage:** Java Serialization and File I/O
- **Date Handling:** `java.time.LocalDate`
- **Testing:** JUnit 5
- **Build Tool:** Java Development Kit (JDK)

## Project Structure

```text
PersonalTaskManager/
├── src/
│   └── com/
│       └── taskmanager/
│           ├── model/
│           │   ├── Task.java
│           │   └── Priority.java
│           ├── service/
│           │   └── TaskService.java
│           └── app/
│               └── SwingTaskManager.java
├── test/
│   └── com/
│       └── taskmanager/
│           ├── model/
│           │   └── TaskTest.java
│           └── service/
│               └── TaskServiceTest.java
├── lib/
│   └── junit-platform-console-standalone.jar
├── data/
│   └── tasks.dat
├── manifest.txt
└── README.md
```

*Note:* The `data/tasks.dat` file is created automatically when tasks are saved. The `out/`, `out-test/`, and executable JAR files are generated during compilation and packaging.

## Prerequisites

- Java JDK 17 or later recommended.
- Windows, Linux, or macOS.
- A terminal or IDE such as IntelliJ IDEA, Eclipse, or VS Code.

Check your Java installation:

```bash
java -version
javac -version
```

## How to Run

### 1. Open the project directory

In Windows PowerShell:

```powershell
cd D:\PersonalTaskManager
```

### 2. Compile the application

```powershell
mkdir out -ErrorAction SilentlyContinue

javac -d out `
  src\com\taskmanager\model\Priority.java `
  src\com\taskmanager\model\Task.java `
  src\com\taskmanager\service\TaskService.java `
  src\com\taskmanager\app\SwingTaskManager.java
```

### 3. Run the application

```powershell
java -cp out com.taskmanager.app.SwingTaskManager
```

The Task Manager desktop window should open.

## How to Use

1. Launch the application.
2. Click **Add Task** to enter task details.
3. Select a due date using the required date format, such as `2026-11-15`.
4. Choose a priority: `LOW`, `MEDIUM`, or `HIGH`.
5. Select a task in the table to update or delete it.
6. Use the search box to find tasks by title or description.
7. Apply priority, status, or due-date filters to narrow the list.
8. Close and reopen the application to verify that saved tasks persist.

## Data Persistence

The application stores task data in a local serialized file, typically:

```text
data/tasks.dat
```

The service loads previously saved tasks at startup and saves changes to disk. Keep this file if you want to retain your task data.

**Important:** The application must have permission to write to the directory containing the data file. Avoid manually editing `tasks.dat`, as it is a serialized Java data file rather than a readable text file.

## Running Unit Tests

The project includes JUnit tests for task validation and file persistence.

Place the JUnit Platform Console Standalone JAR in the `lib/` directory, then run the following commands in PowerShell:

```powershell
mkdir out-test -ErrorAction SilentlyContinue

javac -cp "out;lib\junit-platform-console-standalone.jar" `
  -d out-test `
  test\com\taskmanager\model\TaskTest.java `
  test\com\taskmanager\service\TaskServiceTest.java

java -jar lib\junit-platform-console-standalone.jar execute `
  --class-path "out;out-test" `
  --scan-class-path
```

The test runner reports how many tests passed or failed.

## Build an Executable JAR

After compiling the source files, create a `manifest.txt` file containing:

```text
Main-Class: com.taskmanager.app.SwingTaskManager

```

Ensure the manifest ends with a newline. Then package the application:

```powershell
jar cfm PersonalTaskManager.jar manifest.txt -C out .
```

Run the JAR:

```powershell
java -jar PersonalTaskManager.jar
```

Java must be installed on the computer to run this JAR. Keep the data directory accessible so the application can save tasks.

## Object-Oriented Design

- **`Task.java`:** Represents a task and stores its details.
- **`Priority.java`:** Defines the supported priority levels.
- **`TaskService.java`:** Implements task operations and file persistence.
- **`SwingTaskManager.java`:** Provides the graphical user interface and handles user interactions.

## Future Improvements

- Task sorting by due date and priority.
- Notifications for approaching deadlines.
- Export and import tasks using CSV.
- Dark mode and customizable themes.
- Maven or Gradle build configuration.
- Automatic backup and restore.

## Learning Outcomes

This project demonstrates practical use of:

- Classes, objects, encapsulation, and enums.
- CRUD operations and separation of responsibilities.
- Java collections and exception handling
