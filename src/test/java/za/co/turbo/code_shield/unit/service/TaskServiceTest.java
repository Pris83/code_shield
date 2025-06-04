package za.co.turbo.code_shield.unit.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.repository.TaskRepository;
import za.co.turbo.code_shield.repository.UserRepository;
import za.co.turbo.code_shield.service.TaskService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest{
    @Spy //partially mock
    private UserRepository userRepository = Mockito.mock(UserRepository.class);

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("testPassword");
        user.setEmail("testuser@email.com");

        task = new Task();
        task.setId(1L);
        task.setTitle("Sample Task");
        task.setDescription("This is a test task");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setDueDate(LocalDateTime.now().plusDays(1));
        task.setCreatedAt(LocalDateTime.now());
        task.setAssignee(user);
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

    @Test
    void shouldReturnTaskById() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task found = taskService.getTaskById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
        verify(taskRepository).findById(1L);
    }

    @Test
    void shouldReturnCachedTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task result = taskService.getTask(1L);

        assertNotNull(result);
        assertEquals("Sample Task", result.getTitle());
        verify(taskRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenCachedTaskNotFound() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> taskService.getTask(42L));

        assertEquals("Task not found with id: 42", ex.getMessage());
        verify(taskRepository).findById(42L);
    }

    @Test
    void shouldUpdateTaskSuccessfully() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task updated = taskService.updateTask(1L, task);

        assertNotNull(updated);
        verify(taskRepository).save(task);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentTask() {
        when(taskRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> taskService.updateTask(1L, task));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void shouldDeleteTaskSuccessfully() {
        doNothing().when(taskRepository).deleteById(1L);

        taskService.deleteTask(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    void shouldReturnAllTasks() {
        List<Task> tasks = List.of(task);
        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskService.getAllTasks();

        assertEquals(1, result.size());
        assertEquals("Sample Task", result.get(0).getTitle());
        verify(taskRepository).findAll();
    }

    @Test
    void shouldFindUserByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Optional<User> found = taskService.findUser("testuser");

        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
        verify(userRepository).findByUsername("testuser");
    }
}
