package za.co.turbo.code_shield.tdd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import za.co.turbo.code_shield.BaseTest;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.repository.TaskRepository;
import za.co.turbo.code_shield.repository.UserRepository;
import za.co.turbo.code_shield.service.TaskService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class TaskTest extends BaseTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private static TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

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
    void createTaskSuccess() {

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.createTask(task);

        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        verify(taskRepository, times(1)).save(task);
    }
}
