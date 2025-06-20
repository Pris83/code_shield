package za.co.turbo.code_shield.unit.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import za.co.turbo.code_shield.BaseTest;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.unit.utils.TaskBuilder;
import za.co.turbo.code_shield.validator.TaskValidator;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TaskValidatorTest extends BaseTest {
    @Autowired
    private TaskValidator taskValidator;

    private Task validTask;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");
        user.setEmail("testuser@email.com");

        validTask = new TaskBuilder()
                .withTitle("Sample Task")
                .withDescription("This is a test task")
                .withStatus(TaskStatus.IN_PROGRESS)
                .withDueDate(LocalDateTime.now().plusDays(1))
                .withCreatedAt(LocalDateTime.now())
                .withAssignee(user)
                .build();
    }

    @Test
    void validate_ValidTask_DoesNotThrowException() {
        assertDoesNotThrow(() -> taskValidator.validate(validTask),
                "Valid task should not throw any validation exceptions");
    }

    @Test
    void validate_EmptyTitle_ThrowsException() {
        validTask.setTitle("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskValidator.validate(validTask),
                "Empty title should throw IllegalArgumentException");
        assertEquals("Task title is required", exception.getMessage());
    }

    @Test
    void validate_NullDueDate_ThrowsException() {
        validTask.setDueDate(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskValidator.validate(validTask),
                "Null due date should throw IllegalArgumentException");
        assertEquals("Due date is required", exception.getMessage());
    }

    @Test
    void validate_PastDueDate_ThrowsException() {
        validTask.setDueDate(LocalDateTime.now().minusDays(1));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskValidator.validate(validTask),
                "Past due date should throw IllegalArgumentException");
        assertEquals("Due date cannot be in the past", exception.getMessage());
    }

    @Test
    void validate_NullStatus_ThrowsException() {
        validTask.setStatus(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskValidator.validate(validTask),
                "Null status should throw IllegalArgumentException");
        assertEquals("Task status is required", exception.getMessage());
    }
}
