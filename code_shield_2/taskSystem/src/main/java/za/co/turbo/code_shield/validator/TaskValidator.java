package za.co.turbo.code_shield.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import za.co.turbo.code_shield.model.Task;

import java.time.LocalDateTime;

@Component
public class TaskValidator {
    private static final Logger logger = LoggerFactory.getLogger(TaskValidator.class);

    public void validate(Task task) {
        logger.debug("Validating task: {}", task);

        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            logger.debug("Task title validation failed: title is null or empty");
            throw new IllegalArgumentException("Task title is required");
        }

        if (task.getDueDate() == null) {
            logger.debug("Task due date validation failed: due date is null");
            throw new IllegalArgumentException("Due date is required");
        } else if (task.getDueDate().isBefore(LocalDateTime.now())) {
            logger.debug("Task due date validation failed: due date is in the past");
            throw new IllegalArgumentException("Due date cannot be in the past");
        }

        if (task.getStatus() == null) {
            logger.debug("Task status validation failed: status is null");
            throw new IllegalArgumentException("Task status is required");
        }

        if (task.getAssignee() == null) {
            logger.debug("Task validation failed: assignee is null");
            throw new IllegalArgumentException("Assignee is required");
        }


        logger.debug("Task validation passed");
    }
}
