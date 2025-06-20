package za.co.turbo.code_shield.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import za.co.turbo.code_shield.model.Notification;
import za.co.turbo.code_shield.model.User;

import java.util.Collections;
import java.util.List;

@Component
public class NotificationScheduler {

    private final NotificationService notificationService;

    public NotificationScheduler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(fixedRate = 60000)
    public void sendScheduledReminders() {
        // Load users with pending tasks
        List<User> users = getUsersWithPendingTasks();

        if (users == null || users.isEmpty()) {
            return;
        }

        for (User user : users) {
            try {
                Notification n = new Notification("Reminder", "You have tasks pending!");
                notificationService.sendNotification(user, n);
            } catch (Exception e) {
                // Log and continue with next user
                System.err.println("Failed to send notification to " + user.getUsername() + ": " + e.getMessage());
            }
        }
    }


    protected List<User> getUsersWithPendingTasks() {
        return Collections.emptyList();
    }
}

