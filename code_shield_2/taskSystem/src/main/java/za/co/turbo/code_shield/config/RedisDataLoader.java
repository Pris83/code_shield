package za.co.turbo.code_shield.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.repository.TaskRepository;

import java.util.List;

@Component
@Profile("!test")
public class RedisDataLoader {

    private final TaskRepository repository;

//    private final StringRedisTemplate redisTemplate;
private final RedisTemplate<String, Object> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(RedisDataLoader.class);

    public RedisDataLoader(TaskRepository repository, RedisTemplate<String, Object> redisTemplate) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void loadDataToRedis() {
        List<Task> tasks = repository.findAll();
        for (Task task : tasks) {
            String key = "tasks::" + task.getId(); // or just task.getId() if using Spring Cache
            redisTemplate.opsForValue().set(key, task);
        }
    }

//    @PostConstruct
//    public void loadDataToRedis() {
//        loadFromSQL();
//    }

    private void loadFromSQL() {
        List<Task> tasks = repository.findAll();

        HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();

        for (Task task : tasks) {
            String key = "task:" + task.getId();
            hashOps.put(key, "title", task.getTitle());
            hashOps.put(key, "status", task.getStatus().name());
            hashOps.put(key, "createdAt", task.getCreatedAt());
            hashOps.put(key, "dueDate", task.getDueDate());
            hashOps.put(key, "assignee", task.getAssignee() != null ? task.getAssignee().getUsername() : "unassigned");
            logger.info("Loaded task {} into Redis", key);
        }
    }
}
