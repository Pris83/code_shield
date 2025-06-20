package za.co.turbo.code_shield.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import za.co.turbo.code_shield.exception.TaskNotFoundException;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.repository.TaskRepository;
import za.co.turbo.code_shield.repository.UserRepository;
import za.co.turbo.code_shield.validator.TaskValidator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Service
@EnableCaching
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskValidator taskValidator;
    private final ObjectMapper objectMapper;

    private final TaskNotificationService taskNotificationService;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, TaskValidator taskValidator, ObjectMapper objectMapper, TaskNotificationService taskNotificationService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskValidator = taskValidator;
        this.objectMapper = objectMapper;
        this.taskNotificationService = taskNotificationService;
    }

    public Optional<User> findUser(String username) {
        return userRepository.findByUsername(username);
    }

    @Cacheable(value = "tasks", key = "#id")
    public Task getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public Task createTask(Task task) {
        taskValidator.validate(task);
        Long assigneeId = task.getAssignee().getId();
        boolean assigneeExists = userRepository.existsById(assigneeId);
        if (!assigneeExists) {
            throw new IllegalArgumentException("Assignee does not exist");
        }
        Task savedTask = taskRepository.save(task);
        taskNotificationService.notifyUser(savedTask.getAssignee());
        return savedTask;
    }

    @CacheEvict(value = "tasks", key = "#id")
    public Task updateTask(Long id, Task task) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Task not found with id: " + id);
        }
        taskValidator.validate(task);
        task.setId(id);
        return taskRepository.save(task);
    }

    @CacheEvict(value = "tasks", key = "#id")
    public void deleteTask(Long id) {
        try {
            taskRepository.deleteById(id);
        } catch (Exception e) {
            throw e;
        }
    }

    @Cacheable(value = "tasks", key = "#id")
    public Task getTaskByIdSafe(Long id) {
        Object result = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        if (result instanceof Task) {
            return (Task) result;
        } else if (result instanceof LinkedHashMap) {
            // Convert LinkedHashMap back to Task
            return objectMapper.convertValue(result, Task.class);
        }

        return (Task) result;
    }

    @Cacheable("tasks")
    public List<Task> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return new ArrayList<>(tasks);
    }

    @Cacheable(value = "tasks")
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }
}
