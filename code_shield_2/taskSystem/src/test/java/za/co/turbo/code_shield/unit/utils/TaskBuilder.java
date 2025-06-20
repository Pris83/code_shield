package za.co.turbo.code_shield.unit.utils;

import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.model.User;

import java.time.LocalDateTime;

public class TaskBuilder {
    private String title = "Sample Task";
    private String description = "Sample Description";
    private TaskStatus status = TaskStatus.IN_PROGRESS;
    private LocalDateTime dueDate = LocalDateTime.now().plusDays(1);
    private LocalDateTime createdAt = LocalDateTime.now();
    private User assignee;

    public TaskBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public TaskBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public TaskBuilder withStatus(TaskStatus status) {
        this.status = status;
        return this;
    }

    public TaskBuilder withDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public TaskBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public TaskBuilder withAssignee(User assignee) {
        this.assignee = assignee;
        return this;
    }

    public Task build() {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setDueDate(dueDate);
        task.setCreatedAt(createdAt);
        task.setAssignee(assignee);
        return task;
    }
}
