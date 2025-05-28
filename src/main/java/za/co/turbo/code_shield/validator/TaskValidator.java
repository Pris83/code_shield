package za.co.turbo.code_shield.validator;

import org.springframework.stereotype.Component;
import za.co.turbo.code_shield.model.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class TaskValidator {
    public void validate(Task task) {
        List<String> errors = new ArrayList<>();

        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            errors.add("Task title is required");
        }

        if (task.getDueDate() == null) {
            errors.add("Due date is required");
        } else if (task.getDueDate().isBefore(LocalDateTime.now())) {
            errors.add("Due date cannot be in the past");
        }

        if (task.getStatus() == null) {
            errors.add("Task status is required");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errors));
        }
    }
}
