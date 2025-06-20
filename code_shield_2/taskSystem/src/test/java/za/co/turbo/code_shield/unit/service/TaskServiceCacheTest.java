package za.co.turbo.code_shield.unit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import za.co.turbo.code_shield.BaseTest;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.repository.TaskRepository;
import za.co.turbo.code_shield.repository.UserRepository;
import za.co.turbo.code_shield.service.TaskService;
import za.co.turbo.code_shield.unit.utils.TaskBuilder;
import za.co.turbo.code_shield.assertions.TaskAssert;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Tag("cache")
@Testcontainers
public class TaskServiceCacheTest extends BaseTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.2.0").withExposedPorts(6379);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskService taskService;

    @SpyBean
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheManager cacheManager;

    private Task task;
    private User user;

    @DynamicPropertySource
    static void overrideRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @BeforeEach
    void setup() {
        cacheManager.getCache("tasks").clear();
        taskRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");
        user.setEmail("testuser@email.com");
        userRepository.save(user);

        task = new TaskBuilder()
                .withTitle("Sample Task")
                .withDescription("This is a test task")
                .withStatus(TaskStatus.IN_PROGRESS)
                .withDueDate(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MILLIS))
                .withCreatedAt(LocalDateTime.now())
                .withAssignee(user)
                .build();
        taskRepository.save(task);
    }

    @Test
    void testCachingWithRedis() {
        Task saved = taskRepository.save(task);

        // First call – fetches from DB
        Task task1 = taskService.getTaskById(saved.getId());
        verify(taskRepository, times(1)).findById(saved.getId());

        // Second call – should come from cache, no new DB call
        Task task2 = taskService.getTaskById(saved.getId());
        verify(taskRepository, times(1)).findById(saved.getId());

        // Verify tasks are equal using custom assertion
        assertThat(task2).usingRecursiveComparison().isEqualTo(task1);

        // Verify Redis cache manually using custom assertion
        Task cachedTask = cacheManager.getCache("tasks").get(saved.getId(), Task.class);

        TaskAssert.assertThat(cachedTask).isNotNull();
        assertThat(cachedTask).usingRecursiveComparison().isEqualTo(task1);
    }

    @Test
    void whenUpdateTask_thenCacheShouldBeEvicted() {
        Task saved = taskRepository.save(task);

        // Cache it
        taskService.getTaskById(saved.getId());

        saved.setStatus(TaskStatus.COMPLETED);
        saved.setDescription("Updated Desc");

        taskService.updateTask(saved.getId(), saved);

        // After update, cache should be evicted and DB called again
        Task fetched = taskService.getTaskById(saved.getId());

        verify(taskRepository, times(2)).findById(saved.getId());

        // Use custom assertions on updated task
        TaskAssert.assertThat(fetched).hasTitle("Sample Task")
                .hasDescription("Updated Desc")
                .hasDueDate(saved.getDueDate())
                .hasStatus(TaskStatus.COMPLETED)
                .hasAssignee(user);
    }

    @Test
    void whenDeleteTask_thenCacheShouldBeEvicted() {
        Task saved = taskRepository.save(task);

        // Cache the task
        taskService.getTaskById(saved.getId());
        verify(taskRepository, times(1)).findById(saved.getId());

        // Delete the task
        taskService.deleteTask(saved.getId());

        // Cache entry should be evicted after delete
        Object cacheEntry = cacheManager.getCache("tasks").get(saved.getId(), Task.class);
        assert cacheEntry == null;
    }
}
