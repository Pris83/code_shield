package za.co.turbo.code_shield.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest{
    @Spy //partially mock
    private UserRepository userRepository = Mockito.mock(UserRepository.class);

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setTitle("Sample Task");
        task.setDescription("This is a test task");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setDueDate(LocalDateTime.now().plusDays(1));
        task.setCreatedAt(LocalDateTime.now());
        task.setAssignee(new User());
    }

    @Test
    void shouldThrowWhenTaskNotFound() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.getTaskById(999L));
        verify(taskRepository).findById(999L);
    }

    @Test
    void shouldSaveTaskSuccessfully() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task saved = taskService.createTask(task);

        assertNotNull(saved);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldSaveTaskSuccessfully_UsingCaptor() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task savedTask = taskService.createTask(task);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());

        Task captured = captor.getValue();
        assertEquals("Sample Task", captured.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, captured.getStatus());
        assertNotNull(savedTask);
    }

    @Test
    void shouldThrowWhenTaskNotFound_UsingArgumentMatcher() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.getTaskById(999L));
        verify(taskRepository).findById(eq(999L)); // using eq matcher
    }
}
