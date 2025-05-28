package za.co.turbo.code_shield.tdd;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import za.co.turbo.code_shield.BaseTest;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.repository.TaskRepository;
import za.co.turbo.code_shield.repository.UserRepository;
import za.co.turbo.code_shield.service.TaskService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class TaskTest extends BaseTest {
    @Autowired
    private UserRepository userRepository;

    @Mock
    private static TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTaskSuccess() {
        //arrange
        User user = userRepository.findByUsername("testuser")
                .orElseThrow(() -> new RuntimeException("Test user not found"));

        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("This is a test task");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setDueDate(LocalDateTime.now().plusDays(3));
        task.setCreatedAt(LocalDateTime.now());
        task.setAssignee(user);

        when(taskRepository.save(task)).thenReturn(task);

        //act
        Task result = taskService.createTask(task);

        //assert
        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        verify(taskRepository, times(1)).save(task);    }
}
