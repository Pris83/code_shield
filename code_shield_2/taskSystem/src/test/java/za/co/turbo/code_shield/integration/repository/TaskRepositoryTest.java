package za.co.turbo.code_shield.integration.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.repository.TaskRepository;
import za.co.turbo.code_shield.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql("/test-data.sql")
public class TaskRepositoryTest {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Task task;

    @BeforeEach
    void setUp() {
        task = taskRepository.findTaskByTitle("Sample Task");
    }

    @Test
    void testSaveAndFindById() {
        entityManager.persist(task);
        entityManager.flush();
        Long id = task.getId();

        Optional<Task> found = taskRepository.findById(id);
        assertTrue(found.isPresent());
    }

    @Test
    void createTask_Success(){
        Task savedTask = taskRepository.save(task);

        Optional<Task> found = taskRepository.findById(savedTask.getId());
        assertTrue(found.isPresent(), "Task should be found in the repository");
        assertEquals("Sample Task", found.get().getTitle());
        assertEquals("john_doe", found.get().getAssignee().getUsername());
    }

    @Test
    void readTask_Success(){
        Optional<Task> found = taskRepository.findById(task.getId());
        assertTrue(found.isPresent());
        assertEquals("Sample Task", found.get().getTitle());
    }

    @Test
    void readTask_NotFound(){
        Optional<Task> found = taskRepository.findById(-1L);
        assertFalse(found.isPresent(), "Task with ID -1 should NOT be found");
    }

    @Test
    void updateTask_Success(){
        task.setTitle("Updated Title");
        task.setStatus(TaskStatus.COMPLETED);
        Task updatedTask = taskRepository.save(task);

        Optional<Task> found = taskRepository.findById(updatedTask.getId());
        assertTrue(found.isPresent());
        assertEquals("Updated Title", found.get().getTitle());
    }

    @Test
    void deleteTask_Success(){
        taskRepository.delete(task);

        Optional<Task> found = taskRepository.findById(task.getId());
        assertFalse(found.isPresent(), "Task should be deleted");
    }

    @Test
    void deleteTask_NotFound(){
        // Deleting a non-existent entity should not throw, but nothing happens
        Task nonExistentTask = new Task();
        nonExistentTask.setId(-1L);
        assertDoesNotThrow(() -> taskRepository.delete(nonExistentTask));
    }
}
