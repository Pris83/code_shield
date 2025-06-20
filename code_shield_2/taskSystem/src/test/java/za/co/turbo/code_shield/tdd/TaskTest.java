package za.co.turbo.code_shield.tdd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import za.co.turbo.code_shield.BaseTest;
import za.co.turbo.code_shield.assertions.TaskAssert;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.repository.TaskRepository;
import za.co.turbo.code_shield.repository.UserRepository;
import za.co.turbo.code_shield.service.TaskNotificationService;
import za.co.turbo.code_shield.service.TaskService;
import za.co.turbo.code_shield.validator.TaskValidator;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TaskTest extends BaseTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private static TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskValidator taskValidator;

    @Mock
    private TaskNotificationService taskNotificationService;

    private Task task;
    private User user;

    @BeforeEach
    void setup(){
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("testuser@email.com");
        user.setPassword("testPassword");

        task = new Task();
        task.setTitle("Test Task");
        task.setDescription("This is a test task");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setDueDate(LocalDateTime.now().plusDays(3));
        task.setCreatedAt(LocalDateTime.now());
        task.setAssignee(user);
    }

    @Test
    void createTaskSuccess_shouldNotifyUser() {
        // Arrange
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        taskService.createTask(task);

        // Assert
        verify(taskRepository, times(1)).save(task);
        verify(taskNotificationService).notifyUser(eq(user));
    }


    @Test
    void createTaskShouldFailWhenValidationFails() {
        doThrow(new IllegalArgumentException("Due date is required")).when(taskValidator).validate(any(Task.class));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        IllegalArgumentException ex = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            taskService.createTask(task);
        });

        assertEquals("Due date is required", ex.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }


}
