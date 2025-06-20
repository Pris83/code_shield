package za.co.turbo.code_shield.unit.utils;

import org.springframework.cache.CacheManager;
import za.co.turbo.code_shield.model.User;
import za.co.turbo.code_shield.repository.UserRepository;

public class TestUtils {

    public static User createUser(UserRepository repo, String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setEmail(username + "@email.com");
        return repo.save(user);
    }

    public static void clearCache(CacheManager cacheManager, String cacheName) {
        cacheManager.getCache(cacheName).clear();
    }
}
