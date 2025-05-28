package za.co.turbo.code_shield.unit.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import za.co.turbo.code_shield.BaseTest;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.validator.TaskValidator;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TaskValidatorTest extends BaseTest {
    @Autowired
    private TaskValidator taskValidator;

    private Task validTask;

    @BeforeEach
    void setUp() {
        validTask = new Task();
        validTask.setTitle("Test Task");
        validTask.setDueDate(LocalDateTime.now().plusDays(1));
        validTask.setStatus(TaskStatus.TODO);
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
