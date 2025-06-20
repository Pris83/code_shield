package za.co.turbo.code_shield.unit.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.turbo.code_shield.exception.TaskNotFoundException;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.repository.TaskRepository;
import za.co.turbo.code_shield.repository.UserRepository;
import za.co.turbo.code_shield.service.TaskNotificationService;
import za.co.turbo.code_shield.service.TaskService;
import za.co.turbo.code_shield.unit.utils.TaskBuilder;
import za.co.turbo.code_shield.validator.TaskValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static za.co.turbo.code_shield.assertions.TaskAssert.assertThat;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Spy
    private UserRepository userRepository = Mockito.mock(UserRepository.class);

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskValidator taskValidator;

    @Mock
    private TaskNotificationService taskNotificationService;

    @InjectMocks
    private TaskService taskService;

    private Task task;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");
        user.setEmail("testuser@email.com");

        task = new TaskBuilder()
                .withTitle("Sample Task")
                .withDescription("This is a test task")
                .withStatus(TaskStatus.IN_PROGRESS)
                .withDueDate(LocalDateTime.now().plusDays(1))
                .withCreatedAt(LocalDateTime.now())
                .withAssignee(user)
                .build();

        task.setId(1L);
    }

    @Test
    void shouldThrowWhenTaskNotFound() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.getTaskById(999L));
        verify(taskRepository).findById(999L);
    }

    @Test
    void shouldSaveTaskSuccessfully() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task saved = taskService.createTask(task);

        assertThat(saved)
                .isNotNull()
                .hasTitle("Sample Task")
                .hasStatus(TaskStatus.IN_PROGRESS)
                .hasAssignee(user);

        verify(taskRepository, times(1)).save(any(Task.class));
        verify(taskValidator).validate(task);
        verify(taskNotificationService).notifyUser(user);
    }

    @Test
    void shouldSaveTaskSuccessfully_UsingCaptor() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task savedTask = taskService.createTask(task);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());

        Task captured = captor.getValue();

        assertThat(captured)
                .hasTitle("Sample Task")
                .hasStatus(TaskStatus.IN_PROGRESS)
                .hasAssignee(user);

        assertThat(savedTask).isNotNull();
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

        assertThat(found)
                .isNotNull()
                .hasTitle("Sample Task")
                .hasAssignee(user);

        verify(taskRepository).findById(1L);
    }

    @Test
    void shouldReturnCachedTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task result = taskService.getTask(1L);

        assertThat(result)
                .isNotNull()
                .hasTitle("Sample Task")
                .hasAssignee(user);

        verify(taskRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenCachedTaskNotFound() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        TaskNotFoundException ex = assertThrows(TaskNotFoundException.class,
                () -> taskService.getTask(42L));

        assertEquals("Task not found with id: 42", ex.getMessage());
        verify(taskRepository).findById(42L);
    }

    @Test
    void shouldUpdateTaskSuccessfully() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task updated = taskService.updateTask(1L, task);

        assertThat(updated)
                .isNotNull()
                .hasTitle("Sample Task")
                .hasStatus(TaskStatus.IN_PROGRESS);

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
        assertThat(result.get(0))
                .hasTitle("Sample Task");

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
