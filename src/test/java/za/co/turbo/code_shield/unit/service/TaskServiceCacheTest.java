package za.co.turbo.code_shield.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.repository.TaskRepository;
import za.co.turbo.code_shield.repository.UserRepository;
import za.co.turbo.code_shield.service.TaskService;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class TaskServiceCacheTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.2.0").withExposedPorts(6379);

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

        // given
        user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");
        user.setEmail("testuser@email.com");
        user = userRepository.save(user);

        task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Caching Test");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setDueDate(LocalDateTime.now().plusDays(1));
        task.setCreatedAt(LocalDateTime.now());
        task.setAssignee(user);
    }

    @Test
    void testCachingWithRedis() {

        Task saved = taskRepository.save(task);

        // when – first call (goes to DB)
        Task task1 = taskService.getTaskById(saved.getId());
        verify(taskRepository, times(1)).findById(saved.getId());

        // when – second call (should hit cache)
        Task task2 = taskService.getTaskById(saved.getId());
        verify(taskRepository, times(1)).findById(saved.getId());  // still 1 call, no new calls

        // then – verify both calls return same data
        assertThat(task2).usingRecursiveComparison().isEqualTo(task1);

        // verify Redis cache manually
        Task cached = cacheManager.getCache("tasks").get(saved.getId(), Task.class);
        assertThat(cached).isNotNull();
        assertThat(cached).usingRecursiveComparison().isEqualTo(task1);
    }

    @Test
    void whenUpdateTask_thenCacheShouldBeEvicted() {

        Task saved = taskRepository.save(task);

        // Cache it
        taskService.getTaskById(saved.getId());

        // Update
        Task updated = new Task();
        updated.setTitle("Updated");
        updated.setDescription("Updated Desc");
        updated.setDueDate(LocalDateTime.now().plusDays(2));
        updated.setStatus(TaskStatus.COMPLETED);

        taskService.updateTask(saved.getId(), updated);

        // Now cache should be evicted and repopulated
        Task fetched = taskService.getTaskById(saved.getId());
        verify(taskRepository, times(1)).findById(saved.getId());  // still 1 call, no new calls

        assertThat(fetched.getTitle()).isEqualTo("Updated");
    }

    @Test
    void whenDeleteTask_thenCacheShouldBeEvicted() {

        Task saved = taskRepository.save(task);

        // Cache
        taskService.getTaskById(saved.getId());
        verify(taskRepository, times(1)).findById(saved.getId());  // still 1 call, no new calls

        // Delete
        taskService.deleteTask(saved.getId());
        verify(taskRepository, times(1)).findById(saved.getId());  // still 1 call, no new calls

        // Check cache
        Object cacheEntry = cacheManager.getCache("tasks").get(saved.getId(), Task.class);
        assertThat(cacheEntry).isNull();
    }
}

