package za.co.turbo.code_shield.service;


import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.repository.TaskRepository;
import za.co.turbo.code_shield.repository.UserRepository;
import za.co.turbo.code_shield.validator.TaskValidator;

import java.util.List;
import java.util.Optional;

@Service
@Cacheable
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskValidator taskValidator;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, TaskValidator taskValidator) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskValidator = taskValidator;
    }

    public Optional<User> findUser(String username) {
        return userRepository.findByUsername(username);
    }

    @Cacheable(value = "tasks", key = "#id")
    public Task getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));
    }

    @CacheEvict(value = "tasks", key = "#result.id")
    public Task createTask(Task task) {
        taskValidator.validate(task);
        return taskRepository.save(task);
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
        taskRepository.deleteById(id);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Cacheable(value = "tasks", key = "#id")
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }
}
